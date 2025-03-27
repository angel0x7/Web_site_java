import javax.swing.*;
import java.awt.*;

class PanierPage extends JPanel {
    public PanierPage() {
        setLayout(new BorderLayout());
        add(new JLabel("🛍️ Panier"), BorderLayout.CENTER);
    }
}