package org.moon.figura.backend;

import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.moon.figura.FiguraMod;
import org.moon.figura.config.Config;
import org.moon.figura.gui.FiguraToast;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.RefilledNumber;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class WebsocketManager extends WebSocketClient {

    public static final Map<Integer, String> ERROR_CODES = new HashMap<>() {{
        put(1000, "Normal Closure");
        put(1001, "Going Away");
        put(1002, "Protocol Error");
        put(1003, "Unsupported Data");
        put(1005, "No Status Received");
        put(1006, "Abnormal Closure");
        put(1007, "Invalid Frame Payload Data");
        put(1008, "Policy Violation");
        put(1009, "Message Too Big");
        put(1010, "Mandatory Ext.");
        put(1011, "Internal Error");
        put(1012, "Service Restart");
        put(1013, "Try Again Later");
        put(1014, "Bad Gateway");
        put(1015, "TLS Handshake");
        put(3000, "Unauthorized");
        put(4000, "Re-Auth");
        put(4001, "Banned ^.^");
        put(4002, "Too Many Connections");
    }};

    //limits //(hey thats us)
    public int maxAvatarSize, maxAvatars;

    public final RefilledNumber pingSize = new RefilledNumber();
    public final RefilledNumber pingRate = new RefilledNumber();

    public final RefilledNumber equip = new RefilledNumber();
    public final RefilledNumber upload = new RefilledNumber();
    public final RefilledNumber download = new RefilledNumber();

    public WebsocketManager() {
        super(URI.create(getBackendAddress()));
    }

    private static String getBackendAddress() {
        //ServerAddress backendIP = ServerAddress.parseString(Config.BACKEND_IP.asString());
        return "ws://death";// + backendIP.getHost() + ":" + backendIP.getPort();
    }

    public void tick() {
        //Tick all refilled numbers
        pingSize.tick();
        pingRate.tick();
        equip.tick();
        upload.tick();
        download.tick();

        DownloadRequest request;
        if (download.use() && (request = NetworkManager.REQUEST_QUEUE.poll()) != null) request.function().run();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        FiguraMod.LOGGER.info("Connecting to " + FiguraMod.MOD_NAME + " ws backend (" + getBackendAddress() + ")");
        send(NetworkManager.authToken);
    }

    @Override
    public void onMessage(String message) {
        if (NetworkManager.websocketDebug)
            FiguraMod.LOGGER.warn("RECEIVE: " + message);
        MessageHandler.handleMessage(message);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        if (NetworkManager.websocketDebug)
            FiguraMod.LOGGER.warn("RAW RECEIVE [" + bytes.remaining() + "b]");
        //MessageHandler.handleMessage(bytes);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        reason = reason.isBlank() ? ERROR_CODES.getOrDefault(code, "Unknown") : reason;

        FiguraMod.LOGGER.info("Closed connection: " + reason);
        FiguraMod.LOGGER.info("Code: " + code + ", Remote: " + remote);
        if (Config.CONNECTION_TOASTS.asBool())
            FiguraToast.sendToast(new FiguraText("backend.disconnected"), reason, FiguraToast.ToastType.ERROR);

        NetworkManager.backendStatus = 1;
        NetworkManager.disconnectedReason = reason + (FiguraMod.DEBUG_MODE ? "\n\nCode: " + code + "\nRemote: " + remote : "");
        NetworkManager.backend = null;

        handleClose(code);
    }

    @Override
    public void onError(Exception e) {
        FiguraMod.LOGGER.warn("", e);
        if (Config.CONNECTION_TOASTS.asBool())
            FiguraToast.sendToast(new FiguraText("backend.disconnected"), FiguraToast.ToastType.ERROR);

        NetworkManager.backendStatus = 1;
        NetworkManager.disconnectedReason = e.getMessage();
        NetworkManager.backend = null;
    }

    @Override
    public void send(String text) {
        if (NetworkManager.websocketDebug)
            FiguraMod.LOGGER.warn("SENT: " + text);
        super.send(text);
    }

    @Override
    public void send(byte[] data) {
        if (NetworkManager.websocketDebug)
            FiguraMod.LOGGER.warn("RAW SENT [" + data.length + "b]");
        super.send(data);
    }

    private void handleClose(int code) {
        switch (code) {
            case 4000 -> NetworkManager.auth(true);
            case 4001 -> NetworkManager.banned = true;
        }
    }
}
