package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import healthyBites.model.FoodItem;

public class SwapSelectionPanel extends JPanel {

    private JList<String> swapOptionsList;
    private DefaultListModel<String> listModel;
    private JButton selectButton;
    private JButton backButton;
    private List<FoodItem> foodItemOptions;
    private Consumer<FoodItem> onSelectConsumer;

    public SwapSelectionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Select a Food Item to Swap"));

        // Center panel for the list of options
        listModel = new DefaultListModel<>();
        swapOptionsList = new JList<>(listModel);
        swapOptionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        swapOptionsList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(swapOptionsList);

       
        // This prevents the panel from becoming too large when there are many items in the list.
        // The main window (JFrame) will no longer resize itself to fit this panel.
        scrollPane.setPreferredSize(new Dimension(400, 200));

        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backButton = new JButton("Back");
        selectButton = new JButton("Select Swap");
        buttonPanel.add(backButton);
        buttonPanel.add(selectButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listener for the select button
        selectButton.addActionListener(e -> {
            int selectedIndex = swapOptionsList.getSelectedIndex();
            if (selectedIndex != -1 && onSelectConsumer != null) {
                // Notify the listener with the selected FoodItem
                onSelectConsumer.accept(foodItemOptions.get(selectedIndex));
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item from the list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * Populates the list with food item suggestions.
     * @param options A list of FoodItem objects to display as swap options.
     */
    public void setSwapOptions(List<FoodItem> options) {
        this.foodItemOptions = options;
        listModel.clear();
        if (options != null) {
            for (FoodItem item : options) {
                // Format the string to be displayed in the list
                String displayText = String.format("%s (%.1f %s)", item.getName(), item.getQuantity(), item.getUnit());
                listModel.addElement(displayText);
            }
        }
    }

    /**
     * Sets the listener that will be called when the user clicks the "Select Swap" button.
     * @param onSelect The consumer that will handle the selected FoodItem.
     */
    public void setOnSelectListener(Consumer<FoodItem> onSelect) {
        this.onSelectConsumer = onSelect;
    }

    /**
     * Adds an ActionListener to the "Back" button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
