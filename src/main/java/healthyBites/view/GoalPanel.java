package healthyBites.view;

import javax.swing.*;
import healthyBites.model.FoodItem;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A JPanel for setting up a food swap goal. It allows a user to select a meal from their
 * history, choose a specific ingredient from that meal to replace, and define one or more
 * nutritional goals for the replacement (e.g., "decrease fat by a little bit").
 * @author HealthyBites Team
 */
@SuppressWarnings("serial")
public class GoalPanel extends JPanel {

    /** Button to cancel the goal setting process. */
    private JButton cancelButton;
    /** Button to trigger the search for replaceable food items based on the defined goals. */
    private JButton getReplaceButton;
    /** Button to add a new goal definition row to the panel. */
    private JButton addGoalButton;
    /** Button to remove the last added goal definition row. */
    private JButton removeGoalButton;
    /** A container for the dynamically added goal definition rows. */
    private JPanel goalContainerPanel;
    /** A list of JPanels, each representing a single goal row's UI. */
    private List<JPanel> goalRowPanel;
    /** A list of combo boxes for selecting a nutrient in each goal row. */
    private List<JComboBox<String>> nutrientComboBox;
    /** A list of combo boxes for selecting an action (increase/decrease) in each goal row. */
    private List<JComboBox<String>> actionComboBox;
    /** A list of combo boxes for selecting an arbitrary intensity (e.g., "by a little bit") in each goal row. */
    private List<JComboBox<String>> intensityArbiComboBox;
    /** A list of combo boxes for selecting the unit for a precise goal in each goal row. */
    private List<JComboBox<String>> unitCombo;
    /** A combo box for selecting the ingredient from the chosen meal to be swapped. */
    private JComboBox<String> ingredientComboBox;
    /** The maximum number of goal rows a user can add. */
    private final int MAX_OPTIONS = 2;
    /** The minimum number of goal rows required. */
    private final int MIN_OPTIONS = 1;

    /** An array to hold the list of available nutrients for the dropdowns. */
    private String[] nutrientList = {"N","U","T"};
    /** An array of available actions for the goal. */
    private String[] actionList = {"increase" , "decrease"};
    /** An array of string representations for precise intensity values, linked to the arbitrary list. */
    private String[] intensityPreciseList = {"5", "10", "15"};
    /** An array of available units for precise goals. */
    private String[] unitList = {"%", "g", "mg"};
    /** An array of user-friendly strings for arbitrary goal intensity. */
    private String[] intensityArbiList = {"by a little bit", "more than normal", "significantly"};

    /** A list of FoodItem objects from the selected meal, available for swapping. */
	private List<FoodItem> ingredientList;
    /** The embedded meal history panel used for meal selection. */
    private MealHistoryPanel forMealSelection;
    /** A list of text fields for specifying a precise goal intensity value. */
    private List<JTextField> preciseField;
    /** A callback action to be executed when a nutrient is selected, used to update available units. */
    private BiConsumer<Integer, String> nutrientSelectionAction;

    /**
     * A private inner class to render items in the nutrient JComboBox.
     * It sets a tooltip for each item to display the full nutrient name,
     * which is helpful if the name is too long to be fully visible in the dropdown.
     */
    private class NutrientComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                component.setToolTipText(value.toString());
            }
            return component;
        }
    }

    /**
     * Constructs the GoalPanel.
     * @param mealHistoryPanel A MealHistoryPanel instance for selecting the meal to modify.
     */
    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
    	this.goalRowPanel = new ArrayList<>();
    	this.nutrientComboBox = new ArrayList<>();
    	this.actionComboBox = new ArrayList<>();
    	this.intensityArbiComboBox = new ArrayList<>();
    	this.preciseField = new ArrayList<>();
    	this.forMealSelection = mealHistoryPanel;
    	this.unitCombo = new ArrayList<>();
    	this.ingredientComboBox = new JComboBox<String>();
        ingredientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent component = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    component.setToolTipText(value.toString());
                }
                return component;
            }
        });

        setLayout(new BorderLayout());

        // Top panel for meal selection
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Meal to Swap from:"));
        topPanel.setPreferredSize(new Dimension(0, 200));
        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        topPanel.add(historyScrollPane, BorderLayout.CENTER);

        // Middle section for ingredient and goal definition
        JPanel middle = new JPanel(new BorderLayout());
        JPanel middleP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        middleP.setBorder(BorderFactory.createTitledBorder("Select the food to swap:"));
        middleP.add(new JLabel("Food Items:"));
        ingredientComboBox.setPreferredSize(new Dimension(200, 25));
        middleP.add(ingredientComboBox);

        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Set Your Goal:"));
        JPanel goalHeaderPanel = new JPanel(new BorderLayout());
        JPanel goalButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addGoalButton = new JButton("+");
        addGoalButton.setToolTipText("Add another nutritional goal");
        removeGoalButton = new JButton("-");
        removeGoalButton.setToolTipText("Remove the last nutritional goal");
        goalButtonPanel.add(addGoalButton);
        goalButtonPanel.add(removeGoalButton);
        goalHeaderPanel.add(goalButtonPanel, BorderLayout.CENTER);
        middlePanel.add(goalHeaderPanel, BorderLayout.NORTH);

        goalContainerPanel = new JPanel();
        goalContainerPanel.setLayout(new BoxLayout(goalContainerPanel, BoxLayout.Y_AXIS));
        JScrollPane goalScrollPane = new JScrollPane(goalContainerPanel);
        goalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        middlePanel.add(goalScrollPane, BorderLayout.CENTER);

        middle.add(middleP, BorderLayout.NORTH);
        middle.add(middlePanel, BorderLayout.CENTER);

        // Bottom panel for action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            if (forMealSelection != null) {
                forMealSelection.clearSelection();
            }
        });
        bottomPanel.add(cancelButton);
        getReplaceButton = new JButton("Get Replaceable Food");
        bottomPanel.add(getReplaceButton);

        add(topPanel, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setupGoalButton();
        addGoalRow();
    }

    /**
     * Creates a new row of UI components for defining a single nutritional goal.
     * @return A JPanel representing the goal row.
     */
    private JPanel createNewGoal() {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Top line for Nutrient and Action
        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JComboBox<String> nutrientList = new JComboBox<>(this.nutrientList);
        nutrientList.setRenderer(new NutrientComboBoxRenderer());
        nutrientList.setPreferredSize(new Dimension(150, 25));
        JComboBox<String> actionList = new JComboBox<>(this.actionList);
        actionList.setPreferredSize(new Dimension(100, 25));
        topLine.add(new JLabel("Nutrient"));
        topLine.add(nutrientList);
        topLine.add(new JLabel("Action:"));
        topLine.add(actionList);
        
        // Bottom line for Intensity
        JPanel bottomLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JComboBox<String> intensityArbiList = new JComboBox<>(this.intensityArbiList);
        intensityArbiList.setPreferredSize(new Dimension(150, 25));
        JComboBox<String> unitList = new JComboBox<>(this.unitList);
        unitList.setPreferredSize(new Dimension(60, 25));
        JTextField preciseText = new JTextField(5);
        preciseText.setText(intensityPreciseList[0]);
        intensityArbiList.addActionListener(e -> {
    		int selected =  intensityArbiList.getSelectedIndex();
    		preciseText.setText(intensityPreciseList[selected].toString());
    		unitList.setSelectedIndex(0);
        });
        bottomLine.add(new JLabel("Intensity (Arbi):"));
        bottomLine.add(intensityArbiList);
        bottomLine.add(new JLabel(" OR "));
        bottomLine.add(new JLabel("Intensity (Precise):"));
        bottomLine.add(preciseText);
        bottomLine.add(unitList);
        
        rowPanel.add(topLine);
        rowPanel.add(bottomLine);
        nutrientComboBox.add(nutrientList);
        actionComboBox.add(actionList);
        intensityArbiComboBox.add(intensityArbiList);
        preciseField.add(preciseText);
        unitCombo.add(unitList);

        return rowPanel;
    }

    /**
     * Adds a new goal definition row to the panel, up to the maximum limit.
     */
    private void addGoalRow() {
        if(nutrientComboBox.size() < MAX_OPTIONS) {
            JPanel newRow = createNewGoal();
            goalRowPanel.add(newRow);
            goalContainerPanel.add(newRow);
            applyNutrientListenerToRow(nutrientComboBox.size() - 1);
            updateButtonState();
            goalContainerPanel.revalidate();
            goalContainerPanel.repaint();
        }
    }

    /**
     * Removes the last goal definition row from the panel, down to the minimum limit.
     */
    private void removeGoalRow() {
    	if(nutrientComboBox.size() > MIN_OPTIONS) {
    		int lastIndex = nutrientComboBox.size() - 1;
    		goalContainerPanel.remove(goalRowPanel.remove(lastIndex));
    		nutrientComboBox.remove(lastIndex);
    		actionComboBox.remove(lastIndex);
    		intensityArbiComboBox.remove(lastIndex);
    		unitCombo.remove(lastIndex);
    		preciseField.remove(lastIndex);
    		updateButtonState();
    		goalContainerPanel.revalidate();
    		goalContainerPanel.repaint();
    	}
    }

    /**
     * Populates the nutrient combo boxes with a list of available nutrients.
     * The list is sorted alphabetically before being displayed.
     * @param nutrientList An array of nutrient names.
     */
    public void setNutrientList(String[] nutrientList) {
    	Arrays.sort(nutrientList);
    	this.nutrientList = nutrientList;
    	for (JComboBox<String> list : nutrientComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(nutrientList));
    	}
    }

    /**
     * Populates the ingredient combo box with food items from the selected meal.
     * @param list A list of FoodItem objects.
     */
    public void setIngredientList(List<FoodItem> list) {
    	this.ingredientList = list;
        ingredientComboBox.removeAllItems();
        if (this.ingredientList != null) {
            for (FoodItem item : this.ingredientList) {
                ingredientComboBox.addItem(item.getName());
            }
        }
    	revalidate();
    }

    /**
     * Sets the available units for a specific goal row's unit combo box.
     * @param rowIndex The index of the goal row.
     * @param units An array of unit strings.
     */
    public void setUnitsForRow(int rowIndex, String[] units) {
        if (rowIndex >= 0 && rowIndex < unitCombo.size()) {
            JComboBox<String> unitBox = unitCombo.get(rowIndex);
            String currentSelection = (String) unitBox.getSelectedItem();
            unitBox.removeAllItems();
            if (units != null) {
                for (String unit : units) {
                    unitBox.addItem(unit);
                }
            }
            unitBox.setSelectedItem(currentSelection);
        }
    }

    /**
     * Sets the callback action to be executed when a nutrient is selected.
     * @param action A BiConsumer taking the row index and the selected nutrient name.
     */
    public void onNutrientSelected(BiConsumer<Integer, String> action) {
        this.nutrientSelectionAction = action;
        for (int i = 0; i < nutrientComboBox.size(); i++) {
            applyNutrientListenerToRow(i);
        }
    }

    /**
     * Applies the nutrient selection listener to a specific goal row.
     * @param rowIndex The index of the goal row.
     */
    private void applyNutrientListenerToRow(int rowIndex) {
        JComboBox<String> nutrientBox = nutrientComboBox.get(rowIndex);
        for (ActionListener al : nutrientBox.getActionListeners()) {
            nutrientBox.removeActionListener(al);
        }
        if (nutrientSelectionAction != null) {
            nutrientBox.addActionListener(e -> {
                String selectedNutrient = (String) nutrientBox.getSelectedItem();
                if (selectedNutrient != null && !selectedNutrient.equals("<pick one>")) {
                    nutrientSelectionAction.accept(rowIndex, selectedNutrient);
                }
            });
        }
    }

    /**
     * Gets the list of selected nutrients from all goal rows.
     * @return A List of selected nutrient names.
     */
    public List<String> getSelectedNutrient() {
    	List<String> nutrient = new ArrayList<>();
    	for(JComboBox<String> list : nutrientComboBox) {
    	    nutrient.add((String) list.getSelectedItem());
    	}
    	return nutrient;
    }

    /**
     * Gets the list of selected actions from all goal rows.
     * @return A List of selected actions (e.g., "increase", "decrease").
     */
    public List<String> getSelectedAction() {
    	List<String> action = new ArrayList<>();
    	for(JComboBox<String> list : actionComboBox) {
    	    action.add((String) list.getSelectedItem());
    	}
    	return action;
    }
    
    /**
     * Resets the text in all precise intensity fields to the default value.
     */
    public void setIntensityPreciseToDefault() {
    	for(JTextField list : preciseField) {
    		list.setText("5");
    	}
    }
    
    /**
     * Gets the list of precise intensity values from all goal rows.
     * @return A List of the text values from the precise intensity fields.
     */
    public List<String> getSelectedIntensityPrecise() {
    	List<String> intensity = new ArrayList<>();
    	for(JTextField list : preciseField) {
    	    intensity.add(list.getText());
    	}
    	return intensity;
    }

    /**
     * Gets the list of selected units from all goal rows.
     * @return A List of selected unit names.
     */
    public List<String> getSelectedUnit() {
    	List<String> unit = new ArrayList<>();
    	for(JComboBox<String> list : unitCombo) {
    	    unit.add((String) list.getSelectedItem());
    	}
    	return unit;
    }

    /**
     * Gets the meal history panel associated with this goal panel.
     * @return The MealHistoryPanel instance.
     */
    public MealHistoryPanel getMealHistorySelection() {
        return this.forMealSelection;
    }
    
    /**
     * Gets the name of the ingredient selected for swapping.
     * @return The selected ingredient name as a String.
     */
    public String getSelectedIngredient() {
        return (String) ingredientComboBox.getSelectedItem();
    }
    
    /**
     * Adds an ActionListener to the 'Cancel' button.
     * @param listener The ActionListener to add.
     */
    public void cancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
    
    /**
     * Adds an ActionListener to the 'Get Replaceable Food' button.
     * @param listener The ActionListener to add.
     */
    public void getReplaceButtonListener(ActionListener listener) {
        getReplaceButton.addActionListener(listener);
    }
    
    /**
     * Sets up listeners for the add/remove goal buttons.
     */
    private void setupGoalButton() {
    	addGoalButton.addActionListener(e -> addGoalRow());
    	removeGoalButton.addActionListener(e -> removeGoalRow());
    }

    /**
     * Updates the enabled state of the add/remove goal buttons based on the current row count.
     */
    private void updateButtonState() {
    	addGoalButton.setEnabled(nutrientComboBox.size() < MAX_OPTIONS);
    	removeGoalButton.setEnabled(nutrientComboBox.size() > MIN_OPTIONS);
    }
}
