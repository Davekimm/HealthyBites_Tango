package healthyBites.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Date;
import java.util.function.BiConsumer;

public class MealPanel extends JPanel {

    private JSpinner todaysDate;
    private JComboBox<String> mealTypeCombo;

    private List<JComboBox<String>> ingredientCombos;
    private List<JTextField> quantityFields;
    private List<JComboBox<String>> unitCombos;
    private List<JPanel> ingredientRowPanels;
    private JPanel ingredientContainerPanel;

    private JButton addToMeal, backButton, addIngredientButton, removeIngredientButton;

    private String[] availableIngredients = {"<pick one>"};
    private String[] availableUnits = {"<pick one>"};
    
    private final int MAX_INGREDIENTS = 4;
    private final int MIN_INGREDIENTS = 1;

    // Action to be executed when an ingredient is selected
    private BiConsumer<Integer, String> ingredientSelectionAction;

    // The constructor accepts the history panel - History Panel injected
    public MealPanel(MealHistoryPanel mealHistoryPanel) {
        // Initialize lists
        ingredientCombos = new ArrayList<>();
        quantityFields = new ArrayList<>();
        unitCombos = new ArrayList<>();
        ingredientRowPanels = new ArrayList<>();
        
        // Set BorderLayout to split area (like GoalPanel)
        setLayout(new BorderLayout());
        
     // Top area with meal history
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Meal History"));
        topPanel.setPreferredSize(new Dimension(0, 150));

        // Wrap the history panel in a scroll pane
        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        topPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        // Middle area with meal input form
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Add New Meal"));
        
        // Create meal info section
        JPanel mealInfoPanel = createMealInfoPanel();
        middlePanel.add(mealInfoPanel, BorderLayout.NORTH);
        
        // Create ingredients section
        JPanel ingredientsSection = createIngredientsSection();
        middlePanel.add(ingredientsSection, BorderLayout.CENTER);
        
        // Bottom area with navigation buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        backButton = new JButton("Back");
        addToMeal = new JButton("Add Meal");
        bottomPanel.add(backButton);
        bottomPanel.add(addToMeal);
        
        // Add all sections
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Setup buttons and add first ingredient row
        setupIngredientButtons();
        addIngredientRow();
    }

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

    private JPanel createIngredientsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header for ingredients
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        headerPanel.add(new JLabel("Ingredient"));
        headerPanel.add(new JLabel("Quantity"));
        headerPanel.add(new JLabel("Unit"));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Container for ingredient rows
        ingredientContainerPanel = new JPanel();
        ingredientContainerPanel.setLayout(new BoxLayout(ingredientContainerPanel, BoxLayout.Y_AXIS));
        
        // Scroll pane for ingredients (in case many are added)
        JScrollPane scrollPane = new JScrollPane(ingredientContainerPanel);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons for adding/removing ingredients
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

    private JPanel createNewIngredientRow() {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        rowPanel.setMaximumSize(new Dimension(600, 40));
        
        JComboBox<String> ingredientCombo = new JComboBox<>(availableIngredients);
        ingredientCombo.setPreferredSize(new Dimension(150, 25));
        
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

    private void updateButtonStates() {
        addIngredientButton.setEnabled(ingredientCombos.size() < MAX_INGREDIENTS);
        removeIngredientButton.setEnabled(ingredientCombos.size() > MIN_INGREDIENTS);
    }

    private void setupIngredientButtons() {
        addIngredientButton.addActionListener(e -> addIngredientRow());
        removeIngredientButton.addActionListener(e -> removeIngredientRow());
    }

    /**
     * Restricts the date spinner to only allow selection of past or present dates.
     */
    public void limitMealDateToToday() {
        if (todaysDate.getModel() instanceof SpinnerDateModel) {
            SpinnerDateModel model = (SpinnerDateModel) todaysDate.getModel();
            model.setEnd(new Date());
        }
    }

    public void onIngredientSelected(BiConsumer<Integer, String> action) {
        this.ingredientSelectionAction = action;
        for (int i = 0; i < ingredientCombos.size(); i++) {
            applyListenerToRow(i);
        }
    }

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
        }
    }

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

    public Date getDate() {
        return (Date) todaysDate.getValue();
    }

    public String getMealType() {
        return (String) mealTypeCombo.getSelectedItem();
    }

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

    public void addToMealButtonListener(ActionListener listener) {
        addToMeal.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

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
}