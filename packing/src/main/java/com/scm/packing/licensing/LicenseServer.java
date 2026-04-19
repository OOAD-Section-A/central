package com.scm.packing.licensing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A lightweight TCP license server that must be running before the main
 * Packing application can start.
 *
 * <p><b>Licensing logic overview:</b></p>
 * <ol>
 *   <li>The user launches this program and enters a valid activation key.</li>
 *   <li>If the key matches the hardcoded secret, the server starts
 *       listening on TCP port {@value #PORT}.</li>
 *   <li>Any connecting client receives the handshake string
 *       {@value #HANDSHAKE} and can proceed.</li>
 *   <li>The main Packing application's {@link LicenseChecker} connects
 *       to this port at startup — if the handshake succeeds, the app
 *       boots normally; otherwise it exits.</li>
 * </ol>
 *
 * <p><b>Multithreading:</b> Each incoming client is handled on its own
 * daemon thread so that the server can service multiple verification
 * attempts concurrently (e.g. during development or automated testing).</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class does one thing:
 * validate the activation key and serve license handshakes.</p>
 *
 * <p><b>Note:</b> This is a standalone process (has its own {@code main}
 * method).  It is <i>not</i> a Singleton — it runs as a separate JVM
 * process.</p>
 */
public class LicenseServer {

    /** TCP port the license server listens on. */
    public static final int PORT = 15151;

    /** Handshake string the client expects to receive. */
    public static final String HANDSHAKE = "ACK_VALID";

    /**
     * The activation key that must be provided to start the server.
     * In a real product this would be validated against a remote
     * licensing authority; here it is a simple constant for demonstration.
     */
    private static final String VALID_ACTIVATION_KEY = "SCM-PACK-2026-XRAY";

    /** Flag to control the accept loop (volatile for thread visibility). */
    private volatile boolean running = true;

    /**
     * Starts the server.  Blocks until the process is killed or the
     * server socket is closed.
     */
    public void start() {
        // -----------------------------------------------------------
        // MULTITHREADING: The main thread runs the accept loop.
        // Each client is dispatched to a short-lived daemon thread.
        // -----------------------------------------------------------
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== SCM Packing License Server ===");
            System.out.println("Listening on port " + PORT + "...");
            System.out.println("Clients will receive handshake: " + HANDSHAKE);
            System.out.println("Press Ctrl+C to stop.\n");

            while (running) {
                Socket client = serverSocket.accept();
                // Handle each client on a daemon thread
                Thread handler = new Thread(() -> handleClient(client), "LicenseClient-Handler");
                handler.setDaemon(true);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("[LicenseServer] Server error: " + e.getMessage());
        }
    }

    /**
     * Sends the handshake to a connected client and closes the socket.
     */
    private void handleClient(Socket client) {
        try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            out.println(HANDSHAKE);
            System.out.println("[LicenseServer] Handshake sent to " + client.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("[LicenseServer] Client error: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) { }
        }
    }

    // ---------------------------------------------------------------
    // Standalone entry point
    // ---------------------------------------------------------------

    /**
     * Prompts for an activation key.  If valid, starts the server.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     SCM Packing — License Activation     ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.print("Enter activation key: ");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String key = reader.readLine();

            if (VALID_ACTIVATION_KEY.equals(key != null ? key.trim() : "")) {
                System.out.println("✓ Activation key accepted.");
                new LicenseServer().start();
            } else {
                System.err.println("✗ Invalid activation key. Server will NOT start.");
                System.err.println("  Please contact your SCM administrator for a valid key.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }
    }
}
