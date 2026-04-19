package com.scm.packing.licensing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Client-side license verification used by the main Packing application
 * at startup.
 *
 * <p><b>Licensing logic:</b> The checker attempts to open a TCP connection
 * to {@code localhost:{@value LicenseServer#PORT}}.  If a
 * {@link LicenseServer} is running and responds with the expected
 * {@value LicenseServer#HANDSHAKE} string, verification succeeds.
 * Otherwise the application must refuse to start.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class only answers one
 * question: "Is the license server running and valid?"</p>
 *
 * <p><b>GRASP – Low Coupling:</b> The checker communicates via a raw TCP
 * socket and a plain-text protocol — it has no compile-time dependency
 * on the {@link LicenseServer} class, only on the port and handshake
 * constants.</p>
 */
public class LicenseChecker {

    /**
     * Verifies that a valid License Server is running.
     *
     * @return {@code true} if the handshake succeeds, {@code false} otherwise
     */
    public boolean isLicenseValid() {
        // -----------------------------------------------------------
        // LICENSING: We connect to the License Server on localhost.
        // If the server is not running, Socket will throw a
        // ConnectException and we return false.
        // -----------------------------------------------------------
        try (Socket socket = new Socket("localhost", LicenseServer.PORT);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            String response = in.readLine();

            if (LicenseServer.HANDSHAKE.equals(response)) {
                System.out.println("[LicenseChecker] ✓ License verified — handshake received.");
                return true;
            } else {
                System.err.println("[LicenseChecker] ✗ Unexpected handshake: " + response);
                return false;
            }
        } catch (Exception e) {
            System.err.println("[LicenseChecker] ✗ License server not reachable on port "
                    + LicenseServer.PORT + ": " + e.getMessage());
            return false;
        }
    }
}
