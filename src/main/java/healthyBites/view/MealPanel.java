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

    private java.util.List<JComboBox<String>> ingredientCombos;
    private java.util.List<JTextField> quantityFields;
    private java.util.List<JComboBox<String>> unitCombos;
    private JPanel ingredientsContainer;

    private JButton addToMeal, backButton, addIngredientButton, removeIngredientButton;

    private String[] availableIngredients = {"<pick one>"};
    private String[] availableUnits = {"<pick one>"};

    // Action to be executed when an ingredient is selected
    private BiConsumer<Integer, String> ingredientSelectionAction;

    // The constructor accepts the history panel - History Panel injected
    public MealPanel(MealHistoryPanel mealHistoryPanel) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ingredientCombos = new ArrayList<>();
        quantityFields = new ArrayList<>();
        unitCombos = new ArrayList<>();

        // Create split pane to show meal input and history side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Left side - Meal input panel
        JPanel mainPanel = createMealInputPanel();
        splitPane.setLeftComponent(mainPanel);

        // The right side is the passed-in, reusable panel.
        splitPane.setRightComponent(mealHistoryPanel);

        add(splitPane, BorderLayout.CENTER);
        
     // reset the divider location to allow for window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(0.5);
                removeComponentListener(this);
            }
        });
    }

     private JPanel createMealInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Add New Meal"));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.add(new JLabel("Meal Date:"));
        todaysDate = new JSpinner(new SpinnerDateModel());
        todaysDate.setEditor(new JSpinner.DateEditor(todaysDate, "yyyy-MM-dd"));
        topPanel.add(todaysDate);
        topPanel.add(new JLabel("Meal Type:"));
        mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        topPanel.add(mealTypeCombo);
        panel.add(topPanel);

        panel.add(Box.createVerticalStrut(10));

        addIngredientButton = new JButton("+");
        addIngredientButton.setToolTipText("Add ingredient row");
        removeIngredientButton = new JButton("-");
        removeIngredientButton.setToolTipText("Remove ingredient row");
        backButton = new JButton("Back");
        addToMeal = new JButton("Add Meal");

        setupIngredientButtons();

        panel.add(createIngredientsPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(removeIngredientButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(backButton);
        buttonPanel.add(addToMeal);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ingredients"));

        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        headerPanel.add(new JLabel("Ingredient"));
        headerPanel.add(new JLabel("Quantity"));
        headerPanel.add(new JLabel("Unit"));
        panel.add(headerPanel, BorderLayout.NORTH);

        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));

        addIngredientRow();

        panel.add(ingredientsContainer, BorderLayout.CENTER);

        return panel;
    }

    private void addIngredientRow() {
        if (ingredientCombos.size() >= 4) {
            JOptionPane.showMessageDialog(this, "Maximum 4 ingredients allowed per meal.");
            return;
        }

        JPanel rowPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JComboBox<String> ingredientCombo = new JComboBox<>(availableIngredients);
        JTextField quantityField = new JTextField();
        JComboBox<String> unitCombo = new JComboBox<>(availableUnits);

        rowPanel.add(ingredientCombo);
        rowPanel.add(quantityField);
        rowPanel.add(unitCombo);

        ingredientCombos.add(ingredientCombo);
        quantityFields.add(quantityField);
        unitCombos.add(unitCombo);

        applyListenerToRow(ingredientCombos.size() - 1);

        ingredientsContainer.add(rowPanel);
        ingredientsContainer.add(Box.createVerticalStrut(5));
        ingredientsContainer.revalidate();
        ingredientsContainer.repaint();

        updateButtonStates();
    }

    private void removeIngredientRow() {
        if (ingredientCombos.size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one ingredient row is required.");
            return;
        }

        int lastIndex = ingredientCombos.size() - 1;
        ingredientCombos.remove(lastIndex);
        quantityFields.remove(lastIndex);
        unitCombos.remove(lastIndex);

        int componentCount = ingredientsContainer.getComponentCount();
        if (componentCount >= 2) {
            ingredientsContainer.remove(componentCount - 1);
            ingredientsContainer.remove(componentCount - 2);
        }

        ingredientsContainer.revalidate();
        ingredientsContainer.repaint();
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (addIngredientButton != null && removeIngredientButton != null) {
            addIngredientButton.setEnabled(ingredientCombos.size() < 4);
            removeIngredientButton.setEnabled(ingredientCombos.size() > 1);
        }
    }

    private void setupIngredientButtons() {
        addIngredientButton.addActionListener(e -> addIngredientRow());
        removeIngredientButton.addActionListener(e -> removeIngredientRow());
        updateButtonStates();
    }

    /**
     * Restricts the date spinner to only allow selection of past or present dates.
     */
    public void limitMealDateToToday() {
        if (todaysDate.getModel() instanceof SpinnerDateModel) {
            SpinnerDateModel model = (SpinnerDateModel) todaysDate.getModel();
            // Setting the 'end' date of the model prevents selection of future dates.
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

        while (ingredientCombos.size() > 1) {
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