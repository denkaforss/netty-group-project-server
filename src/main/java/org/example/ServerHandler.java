package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.URI;
import java.time.LocalDate;
import java.awt.Desktop;

public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final AttributeKey<String> USERNAME = AttributeKey.valueOf("username");
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byte packageId = byteBuf.readByte();
        if (packageId == 0) {
            String message = readString(byteBuf);
            System.out.println("Client message: " + message);
            //channelHandlerContext.channel().attr(USERNAME).set(message);
        } else if (packageId == 1) {
            Desktop desk = Desktop.getDesktop();
            String url = readString(byteBuf);
            System.out.println("Please open this url. We have cookies: " + url);
            desk.browse(new URI(url));
        } else if (packageId == 2) {
              LocalDate date = LocalDate.now();
              ByteBuf dateBuffer = Unpooled.buffer();
              dateBuffer.writeByte(2);
              dateBuffer.writeInt(date.toString().length());
              dateBuffer.writeBytes(date.toString().getBytes());
              channels.writeAndFlush(dateBuffer);
        } else if (packageId == 3) {
            System.out.println("╭∩╮ (òÓ,) ╭∩╮");
        }
    }

    public static String readString(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        byte[] buffer = new byte[length];
        byteBuf.readBytes(buffer, 0, length);
        return new String(buffer, 0, length);
    }
}
