package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import healthyBites.model.FoodItem;

/**
 * A JPanel that displays a list of suggested food items for a swap.
 * Users can select one item from the list and confirm their choice to apply the swap.
 * @author HealthyBites Team
 */
public class SwapSelectionPanel extends JPanel {

    /** The JList component to display the swap options. */
    private JList<String> swapOptionsList;
    /** The data model for the swapOptionsList. */
    private DefaultListModel<String> listModel;
    /** The button to confirm the selection and apply the swap. */
    private JButton selectButton;
    /** The button to navigate back to the previous screen without making a selection. */
    private JButton backButton;
    /** A list of FoodItem objects that correspond to the options displayed in the JList. */
    private List<FoodItem> foodItemOptions;
    /** A callback function to be executed when a food item is selected. */
    private Consumer<FoodItem> onSelectConsumer;

    /**
     * Constructs the SwapSelectionPanel, initializing the UI components,
     * including the list for swap options and the navigation buttons.
     */
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
        scrollPane.setPreferredSize(new Dimension(400, 200));

        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backButton = new JButton("Back");
        selectButton = new JButton("Apply Swap");
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
     * Populates the list with food item suggestions for the swap.
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
     * Sets the listener that will be called when the user clicks the "Apply Swap" button.
     * @param onSelect The consumer function that will handle the selected FoodItem.
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