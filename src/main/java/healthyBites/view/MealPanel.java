package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * A JPanel that provides a form for users to log a new meal.
 * It includes fields for the meal date and type, and a dynamic list of ingredients where users
 * can add or remove ingredient rows. It also displays a meal history panel at the top.
 * @author HealthyBites Team
 */
public class MealPanel extends JPanel {

    /** Spinner for selecting the meal's date. */
    private JSpinner todaysDate;
    /** ComboBox for selecting the meal type (e.g., Breakfast, Lunch). */
    private JComboBox<String> mealTypeCombo;

    /** A list of JComboBoxes, one for each ingredient row. */
    private List<JComboBox<String>> ingredientCombos;
    /** A list of JTextFields for ingredient quantities. */
    private List<JTextField> quantityFields;
    /** A list of JComboBoxes for ingredient units. */
    private List<JComboBox<String>> unitCombos;
    /** A list of JPanels, where each panel represents an entire ingredient row. */
    private List<JPanel> ingredientRowPanels;
    /** The container panel that holds all the ingredient row panels. */
    private JPanel ingredientContainerPanel;

    /** Buttons for submitting the meal, going back, and managing ingredient rows. */
    private JButton addToMeal, backButton, addIngredientButton, removeIngredientButton;

    /** The list of available ingredients to populate the ingredient combo boxes. */
    private String[] availableIngredients = {"<pick one>"};
    /** The list of available units to populate the unit combo boxes. */
    private String[] availableUnits = {"<pick one>"};
    
    /** The maximum number of ingredients allowed per meal. */
    private final int MAX_INGREDIENTS = 4;
    /** The minimum number of ingredients required per meal. */
    private final int MIN_INGREDIENTS = 1;
    /** The maximum number of characters to display in the ingredient combo box before truncating with "...". */
    private final int MAX_INGREDIENT_DISPLAY_LENGTH = 20;

    /** MealHistoryPanel object in Log Meal page */
    private MealHistoryPanel forMealSelection;
    
    /** A callback function to execute when an ingredient is selected from a combo box. */
    private BiConsumer<Integer, String> ingredientSelectionAction;

    /**
     * A custom renderer for the ingredient JComboBox. It truncates long ingredient names for display
     * and shows the full name in a tooltip.
     */
    private class IngredientComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value != null) {
                String fullText = value.toString();
                String displayText = truncateText(fullText, MAX_INGREDIENT_DISPLAY_LENGTH);
                
                setText(displayText);
                setToolTipText(fullText.equals(displayText) ? null : fullText);
            }
            
            return this;
        }
        
        /**
         * Truncates a string if it exceeds a maximum length.
         * @param text The text to truncate.
         * @param maxLength The maximum allowed length.
         * @return The truncated text, or the original text if it's within the limit.
         */
        private String truncateText(String text, int maxLength) {
            if (text.length() <= maxLength) {
                return text;
            }
            return text.substring(0, maxLength - 3) + "...";
        }
    }

    /**
     * Constructs the MealPanel.
     * @param mealHistoryPanel A MealHistoryPanel instance to be displayed at the top of this panel.
     */
    public MealPanel(MealHistoryPanel mealHistoryPanel) {
        ingredientCombos = new ArrayList<>();
        quantityFields = new ArrayList<>();
        unitCombos = new ArrayList<>();
        ingredientRowPanels = new ArrayList<>();
        this.forMealSelection = mealHistoryPanel;
        
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Meal History"));
        topPanel.setPreferredSize(new Dimension(0, 200));

        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        topPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Add New Meal"));
        
        JPanel mealInfoPanel = createMealInfoPanel();
        middlePanel.add(mealInfoPanel, BorderLayout.NORTH);
        
        JPanel ingredientsSection = createIngredientsSection();
        middlePanel.add(ingredientsSection, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        backButton = new JButton("Back");
        addToMeal = new JButton("Add Meal");
        bottomPanel.add(backButton);
        bottomPanel.add(addToMeal);
        
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setupIngredientButtons();
        addIngredientRow();
    }

    /**
     * Creates the panel for meal date and type selection.
     * @return A JPanel containing the meal info controls.
     */
    private JPanel createMealInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        panel.add(new JLabel("Meal Date:"));
        todaysDate = new JSpinner(new SpinnerDateModel());
        todaysDate.setEditor(new JSpinner.DateEditor(todaysDate, "yyyy-MM-dd"));
        JComponent dateEditor = todaysDate.getEditor();
        Dimension dateSize = dateEditor.getPreferredSize();
        dateSize.width = 120;
        dateEditor.setPreferredSize(dateSize);
        panel.add(todaysDate);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("Meal Type:"));
        mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        panel.add(mealTypeCombo);
        
        return panel;
    }

    /**
     * Creates the main section for adding and managing ingredients.
     * @return A JPanel containing the ingredient input area and controls.
     */
    private JPanel createIngredientsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        headerPanel.add(new JLabel("Ingredient"));
        headerPanel.add(new JLabel("Quantity"));
        headerPanel.add(new JLabel("Unit"));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        ingredientContainerPanel = new JPanel();
        ingredientContainerPanel.setLayout(new BoxLayout(ingredientContainerPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(ingredientContainerPanel);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addIngredientButton = new JButton("+");
        addIngredientButton.setToolTipText("Add ingredient row");
        removeIngredientButton = new JButton("-");
        removeIngredientButton.setToolTipText("Remove ingredient row");
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(removeIngredientButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates a single new row for entering one ingredient, its quantity, and unit.
     * @return A JPanel representing the new ingredient row.
     */
    private JPanel createNewIngredientRow() {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        rowPanel.setMaximumSize(new Dimension(600, 40));
        
        JComboBox<String> ingredientCombo = new JComboBox<>(availableIngredients);
        ingredientCombo.setPreferredSize(new Dimension(150, 25));
        ingredientCombo.setRenderer(new IngredientComboBoxRenderer());
        
        JTextField quantityField = new JTextField(8);
        
        JComboBox<String> unitCombo = new JComboBox<>(availableUnits);
        unitCombo.setPreferredSize(new Dimension(100, 25));

        rowPanel.add(ingredientCombo);
        rowPanel.add(quantityField);
        rowPanel.add(unitCombo);

        ingredientCombos.add(ingredientCombo);
        quantityFields.add(quantityField);
        unitCombos.add(unitCombo);
        
        applyListenerToRow(ingredientCombos.size() - 1);

        return rowPanel;
    }

    /**
     * Adds a new ingredient row to the form, up to the maximum limit.
     */
    private void addIngredientRow() {
        if (ingredientCombos.size() < MAX_INGREDIENTS) {
            JPanel newRow = createNewIngredientRow();
            ingredientRowPanels.add(newRow);
            ingredientContainerPanel.add(newRow);
            
            updateButtonStates();
            ingredientContainerPanel.revalidate();
            ingredientContainerPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Maximum " + MAX_INGREDIENTS + " ingredients allowed per meal.");
        }
    }

    /**
     * Removes the last ingredient row from the form, down to the minimum limit.
     */
    private void removeIngredientRow() {
        if (ingredientCombos.size() > MIN_INGREDIENTS) {
            int lastIndex = ingredientCombos.size() - 1;
            
            ingredientContainerPanel.remove(ingredientRowPanels.remove(lastIndex));
            ingredientCombos.remove(lastIndex);
            quantityFields.remove(lastIndex);
            unitCombos.remove(lastIndex);

            updateButtonStates();
            ingredientContainerPanel.revalidate();
            ingredientContainerPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "At least one ingredient row is required.");
        }
    }

    /**
     * Enables or disables the add/remove ingredient buttons based on the current number of rows.
     */
    private void updateButtonStates() {
        addIngredientButton.setEnabled(ingredientCombos.size() < MAX_INGREDIENTS);
        removeIngredientButton.setEnabled(ingredientCombos.size() > MIN_INGREDIENTS);
    }

    /**
     * Sets up the ActionListeners for the add and remove ingredient buttons.
     */
    private void setupIngredientButtons() {
        addIngredientButton.addActionListener(e -> addIngredientRow());
        removeIngredientButton.addActionListener(e -> removeIngredientRow());
    }

    /**
     * Restricts the date spinner to only allow selection of past or present dates.
     */
    public void limitMealDateToToday() {
        if (todaysDate.getModel() instanceof SpinnerDateModel model) {
            model.setEnd(new Date());
        }
    }

    /**
     * Sets the callback action to be executed when an ingredient is selected.
     * @param action A BiConsumer that takes the row index (Integer) and selected ingredient name (String).
     */
    public void onIngredientSelected(BiConsumer<Integer, String> action) {
        this.ingredientSelectionAction = action;
        for (int i = 0; i < ingredientCombos.size(); i++) {
            applyListenerToRow(i);
        }
    }

    /**
     * Applies an ActionListener to the ingredient combo box of a specific row.
     * @param rowIndex The index of the row to apply the listener to.
     */
    private void applyListenerToRow(int rowIndex) {
        JComboBox<String> ingredientCombo = ingredientCombos.get(rowIndex);

        for (ActionListener al : ingredientCombo.getActionListeners()) {
            ingredientCombo.removeActionListener(al);
        }

        if (this.ingredientSelectionAction != null) {
            ingredientCombo.addActionListener(e -> {
                String selectedIngredient = (String) ingredientCombo.getSelectedItem();
                if (selectedIngredient != null && !selectedIngredient.equals("<pick one>")) {
                    ingredientSelectionAction.accept(rowIndex, selectedIngredient);
                } else {
                    setUnitsForRow(rowIndex, new String[]{});
                }
            });
        }
    }

    /**
     * Populates the unit combo box for a specific ingredient row with a list of valid units.
     * @param rowIndex The index of the row to update.
     * @param units An array of unit strings to display.
     */
    public void setUnitsForRow(int rowIndex, String[] units) {
        if (rowIndex >= 0 && rowIndex < unitCombos.size()) {
            JComboBox<String> unitCombo = unitCombos.get(rowIndex);
            String currentSelection = (String) unitCombo.getSelectedItem();

            unitCombo.removeAllItems();
            unitCombo.addItem("<pick one>");
            if (units != null) {
                for (String unit : units) {
                    unitCombo.addItem(unit);
                }
            }
            
            unitCombo.setSelectedItem(currentSelection);
        }
    }
    
    /**
     * Sets the list of available ingredients for all ingredient combo boxes.
     * @param ingredients An array of ingredient names.
     */
    public void setAvailableIngredients(String[] ingredients) {
        String[] fullIngredientList = new String[ingredients.length + 1];
        fullIngredientList[0] = "<pick one>";
        System.arraycopy(ingredients, 0, fullIngredientList, 1, ingredients.length);

        this.availableIngredients = fullIngredientList;

        for (JComboBox<String> combo : ingredientCombos) {
            String currentSelection = (String) combo.getSelectedItem();
            combo.removeAllItems();
            for (String ingredient : this.availableIngredients) {
                combo.addItem(ingredient);
            }
            combo.setSelectedItem(currentSelection);
            combo.setRenderer(new IngredientComboBoxRenderer());
        }
    }

    /**
     * Sets the list of available units for all unit combo boxes.
     * @param units An array of unit names.
     */
    public void setAvailableUnits(String[] units) {
        this.availableUnits = units;
        for (JComboBox<String> combo : unitCombos) {
            String currentSelection = (String) combo.getSelectedItem();
            combo.removeAllItems();
            for (String unit : units) {
                combo.addItem(unit);
            }
            if (currentSelection != null) {
                combo.setSelectedItem(currentSelection);
            }
        }
    }

    /**
     * Gets the selected meal date.
     * @return The meal date as a Date object.
     */
    public Date getDate() {
        return (Date) todaysDate.getValue();
    }

    /**
     * Gets the selected meal type.
     * @return The meal type as a String.
     */
    public String getMealType() {
        return (String) mealTypeCombo.getSelectedItem();
    }

    /**
     * Gets a list of all validly entered ingredients.
     * @return A List of ingredient name strings.
     */
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCombos.size(); i++) {
            String ingredient = (String) ingredientCombos.get(i).getSelectedItem();
            String quantity = quantityFields.get(i).getText().trim();
            String unit = (String) unitCombos.get(i).getSelectedItem();
            
            if (ingredient != null && !ingredient.equals("<pick one>") &&
                !quantity.isEmpty() &&
                unit != null && !unit.equals("<pick one>")) {
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    /**
     * Gets a list of all validly entered quantities.
     * @return A List of quantity strings.
     */
    public List<String> getQuantities() {
        List<String> quantities = new ArrayList<>();
        for (int i = 0; i < quantityFields.size(); i++) {
            String ingredient = (String) ingredientCombos.get(i).getSelectedItem();
            String quantity = quantityFields.get(i).getText().trim();
            String unit = (String) unitCombos.get(i).getSelectedItem();

            if (ingredient != null && !ingredient.equals("<pick one>") &&
                !quantity.isEmpty() &&
                unit != null && !unit.equals("<pick one>")) {
                quantities.add(quantity);
            }
        }
        return quantities;
    }

    /**
     * Gets a list of all validly entered units.
     * @return A List of unit strings.
     */
    public List<String> getUnits() {
        List<String> units = new ArrayList<>();
        for (int i = 0; i < unitCombos.size(); i++) {
            String ingredient = (String) ingredientCombos.get(i).getSelectedItem();
            String quantity = quantityFields.get(i).getText().trim();
            String unit = (String) unitCombos.get(i).getSelectedItem();
            
            if (ingredient != null && !ingredient.equals("<pick one>") &&
                !quantity.isEmpty() &&
                unit != null && !unit.equals("<pick one>")) {
                units.add(unit);
            }
        }
        return units;
    }

    /**
     * Adds an ActionListener to the 'Add Meal' button.
     * @param listener The ActionListener to add.
     */
    public void addToMealButtonListener(ActionListener listener) {
        addToMeal.addActionListener(listener);
    }

    /**
     * Adds an ActionListener to the 'Back' button.
     * @param listener The ActionListener to add.
     */
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    /**
     * Clears all fields in the form, resetting it to its initial state.
     */
    public void clearFields() {
        todaysDate.setValue(new Date());
        mealTypeCombo.setSelectedIndex(0);

        while (ingredientCombos.size() > MIN_INGREDIENTS) {
            removeIngredientRow();
        }

        if (!ingredientCombos.isEmpty()) {
            ingredientCombos.get(0).setSelectedIndex(0);
            quantityFields.get(0).setText("");
            unitCombos.get(0).removeAllItems();
            unitCombos.get(0).addItem("<pick one>");
        }
    }
    
    /**
     *  Returns the MealHistoryPanel object in the Log Meal page.
     *  
     *  @return MealHistoryPanel object
     */
    public MealHistoryPanel getMealHistorySelection() {
    	return this.forMealSelection;
    }
    
    /**
     * Returns the total number of ingredient rows that user created.
     * 
     * @return the number of ingredient rows.
     */
    public int getNumberOfIngredientRows() {
        return ingredientCombos.size();
    }
}