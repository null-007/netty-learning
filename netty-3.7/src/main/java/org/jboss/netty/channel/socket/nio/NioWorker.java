/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.util.ThreadNameDeterminer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;

import static org.jboss.netty.channel.Channels.*;

public class NioWorker extends AbstractNioWorker {

    private final SocketReceiveBufferAllocator recvBufferPool = new SocketReceiveBufferAllocator();

    public NioWorker(Executor executor) {
        super(executor);
    }

    public NioWorker(Executor executor, ThreadNameDeterminer determiner) {
        super(executor, determiner);
    }

    @Override
    protected boolean read(SelectionKey k) {
        final SocketChannel ch = (SocketChannel) k.channel();
        final NioSocketChannel channel = (NioSocketChannel) k.attachment();

        final ReceiveBufferSizePredictor predictor =
            channel.getConfig().getReceiveBufferSizePredictor();
        final int predictedRecvBufSize = predictor.nextReceiveBufferSize();
        final ChannelBufferFactory bufferFactory = channel.getConfig().getBufferFactory();

        int ret = 0;
        int readBytes = 0;
        boolean failure = true;

        ByteBuffer bb = recvBufferPool.get(predictedRecvBufSize).order(bufferFactory.getDefaultOrder());
        try {
            while ((ret = ch.read(bb)) > 0) {
                readBytes += ret;
                if (!bb.hasRemaining()) {
                    break;
                }
            }
            failure = false;
        } catch (ClosedChannelException e) {
            // Can happen, and does not need a user attention.
        } catch (Throwable t) {
            fireExceptionCaught(channel, t);
        }

        if (readBytes > 0) {
            bb.flip();

            final ChannelBuffer buffer = bufferFactory.getBuffer(readBytes);
            buffer.setBytes(0, bb);
            buffer.writerIndex(readBytes);

            // Update the predictor.
            predictor.previousReceiveBufferSize(readBytes);

            // Fire the event.
            fireMessageReceived(channel, buffer);
        }

        if (ret < 0 || failure) {
            k.cancel(); // Some JDK implementations run into an infinite loop without this.
            close(channel, succeededFuture(channel));
            return false;
        }

        return true;
    }

    @Override
    protected boolean scheduleWriteIfNecessary(final AbstractNioChannel<?> channel) {
        final Thread currentThread = Thread.currentThread();
        final Thread workerThread = thread;
        if (currentThread != workerThread) {
            if (channel.writeTaskInTaskQueue.compareAndSet(false, true)) {
                registerTask(channel.writeTask);
            }

            return true;
        }

        return false;
    }

    @Override
    protected Runnable createRegisterTask(Channel channel, ChannelFuture future) {
        boolean server = !(channel instanceof NioClientSocketChannel);
        return new RegisterTask((NioSocketChannel) channel, future, server);
    }

    private final class RegisterTask implements Runnable {
        private final NioSocketChannel channel;
        private final ChannelFuture future;
        private final boolean server;

        RegisterTask(
                NioSocketChannel channel, ChannelFuture future, boolean server) {

            this.channel = channel;
            this.future = future;
            this.server = server;
        }

        public void run() {
            SocketAddress localAddress = channel.getLocalAddress();
            SocketAddress remoteAddress = channel.getRemoteAddress();

            if (localAddress == null || remoteAddress == null) {
                if (future != null) {
                    future.setFailure(new ClosedChannelException());
                }
                close(channel, succeededFuture(channel));
                return;
            }

            try {
                if (server) {
                    channel.channel.configureBlocking(false);
                }
                //  注册
                channel.channel.register(
                        selector, channel.getRawInterestOps(), channel);

                if (future != null) {
                    channel.setConnected();
                    future.setSuccess();
                }

                if (server || !((NioClientSocketChannel) channel).boundManually) {
                    fireChannelBound(channel, localAddress);
                }
                fireChannelConnected(channel, remoteAddress);
            } catch (IOException e) {
                if (future != null) {
                    future.setFailure(e);
                }
                close(channel, succeededFuture(channel));
                if (!(e instanceof ClosedChannelException)) {
                    throw new ChannelException(
                            "Failed to register a socket to the selector.", e);
                }
            }
        }
    }

    @Override
    public void run() {
        super.run();
        recvBufferPool.releaseExternalResources();
    }
}
