package com.scm.packing;

import com.scm.packing.integration.database.DatabaseLayerFactory;
import com.scm.packing.integration.database.IDatabaseLayer;
import com.scm.packing.integration.exceptions.ExceptionDispatcherFactory;
import com.scm.packing.integration.exceptions.IExceptionDispatcher;
import com.scm.packing.licensing.LicenseChecker;
import com.scm.packing.mvc.controller.PackingController;
import com.scm.packing.mvc.model.PackingModel;
import com.scm.packing.mvc.view.PackingMainFrame;
import com.scm.packing.strategy.PackingStrategyFactory;

import javax.swing.*;

/**
 * Application entry point for the SCM Packing Subsystem.
 *
 * <p><b>Startup sequence:</b></p>
 * <ol>
 *   <li><b>License check</b> — Verify that the {@link com.scm.packing.licensing.LicenseServer}
 *       process is running.  If not, display an error dialog and exit.</li>
 *   <li><b>Integration bootstrap</b> — Use factories to create the
 *       database layer and exception dispatcher with automatic fallback.</li>
 *   <li><b>MVC wiring</b> — Instantiate Model → Controller → View with
 *       constructor injection (no Singletons).</li>
 *   <li><b>Show UI</b> — Display the Swing frame on the EDT.</li>
 * </ol>
 *
 * <p><b>SOLID – Dependency Inversion:</b> Every dependency is created
 * through factory abstractions and injected via constructors.  Nothing
 * is hardcoded to a concrete adapter class.</p>
 *
 * <p><b>Multithreading:</b> The Swing UI is created on the EDT via
 * {@link SwingUtilities#invokeLater} as required by the Swing threading
 * contract.</p>
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {

        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     SCM Packing Subsystem — Starting     ║");
        System.out.println("╚═══════════════════════════════════════════╝");

        // ===========================================================
        // STEP 1: LICENSE VERIFICATION
        // The LicenseChecker connects to the LicenseServer on
        // localhost:15151.  If the server is not running or the
        // handshake fails, the application must not proceed.
        // ===========================================================
        System.out.println("\n[Main] Step 1 — Checking software license...");

        LicenseChecker licenseChecker = new LicenseChecker();
        if (!licenseChecker.isLicenseValid()) {
            System.err.println("[Main] ✗ License verification failed.");
            System.err.println("[Main]   Please start the License Server with a valid activation key.");
            System.err.println("[Main]   Run: java com.scm.packing.licensing.LicenseServer");

            // Show a blocking Swing dialog before exiting
            // (safe even before full UI init — Swing auto-creates the EDT)
            JOptionPane.showMessageDialog(null,
                    "Software license is invalid or the License Server is not running.\n\n"
                    + "Please start the License Server with a valid activation key and try again.\n"
                    + "Command:  java com.scm.packing.licensing.LicenseServer",
                    "SCM Packing — License Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
        }

        System.out.println("[Main] ✓ License OK.\n");

        // ===========================================================
        // STEP 2: INTEGRATION BOOTSTRAP (with automatic fallback)
        // Factories detect whether external subsystem JARs are present
        // and return the correct adapter.
        // ===========================================================
        System.out.println("[Main] Step 2 — Bootstrapping integration layers...");

        // --- Database layer ---
        // Uses SCMDatabaseAdapter if database-module JAR is on classpath,
        // otherwise falls back to FlatFileDatabaseAdapter.
        IDatabaseLayer databaseLayer = DatabaseLayerFactory.create();

        // --- Exception dispatcher ---
        // Uses SCMExceptionAdapter if exception handler JARs are present,
        // otherwise falls back to FallbackConsoleLogger.
        IExceptionDispatcher exceptionDispatcher = ExceptionDispatcherFactory.create();

        System.out.println("[Main] ✓ Integration layers ready.\n");

        // ===========================================================
        // STEP 3: MVC WIRING via Constructor Injection
        // No Singletons — every dependency is passed explicitly.
        // ===========================================================
        System.out.println("[Main] Step 3 — Wiring MVC components...");

        // Model (the subject in Observer pattern)
        PackingModel model = new PackingModel(databaseLayer, exceptionDispatcher);

        // Strategy factory (Creational – Factory Method pattern)
        PackingStrategyFactory strategyFactory = new PackingStrategyFactory();

        // Controller (GRASP Controller)
        PackingController controller = new PackingController(model, strategyFactory);

        // Load orders from DB (or seed data) and any persisted jobs
        controller.loadInitialData();

        System.out.println("[Main] ✓ MVC components wired.\n");

        // ===========================================================
        // STEP 4: LAUNCH SWING UI ON THE EDT
        //
        // MULTITHREADING: Swing components must be created on the
        // Event Dispatch Thread.  SwingUtilities.invokeLater ensures
        // this contract is honoured.
        // ===========================================================
        System.out.println("[Main] Step 4 — Launching UI on EDT...\n");

        SwingUtilities.invokeLater(() -> {
            // Apply FlatLaf look-and-feel for modern aesthetics
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } catch (Exception e) {
                System.err.println("[Main] FlatLaf not available — using system L&F.");
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) { }
            }

            // Create and display the main frame (View + Observer)
            PackingMainFrame frame = new PackingMainFrame(model, controller);
            frame.setVisible(true);

            model.publishStatus("Application started — ready to process packing jobs.");
        });
    }
}
