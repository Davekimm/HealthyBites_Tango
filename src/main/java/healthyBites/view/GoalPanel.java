package healthyBites.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import healthyBites.model.FoodItem;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GoalPanel extends JPanel {

    private JButton cancelButton, getReplaceButton, addGoalButton, removeGoalButton;
    private JPanel goalContainerPanel;
    private List<JPanel> goalRowPanel;
    private List<JComboBox<String>> nutrientComboBox, actionComboBox, intensityArbiComboBox,
    								unitCombo;
    private JComboBox<String> ingredientComboBox;
    private final int MAX_OPTIONS = 2;
    private final int MIN_OPTIONS = 1;

    private String[] nutrientList = {"N","U","T"}, actionList = {"increase" , "decrease"},
    				intensityPreciseList = {"5", "10", "15"}, unitList = {"%", "g", "mg"},
    				intensityArbiList = {"by a little bit", "more than normal", "significantly"};

	private List<FoodItem> ingredientList;
    private MealHistoryPanel forMealSelection;
    private List<JTextField> preciseField;
    private BiConsumer<Integer, String> nutrientSelectionAction;

    public GoalPanel(MealHistoryPanel mealHistoryPanel) {
        //initialize
    	this.goalRowPanel = new ArrayList<>();
    	this.nutrientComboBox = new ArrayList<>();
    	this.actionComboBox = new ArrayList<>();
    	this.intensityArbiComboBox = new ArrayList<>();
    	this.preciseField = new ArrayList<>();
    	this.forMealSelection = mealHistoryPanel;
    	this.unitCombo = new ArrayList<>();
    	this.ingredientComboBox = new JComboBox<String>();
        // Add a custom renderer to show tooltips for long item names
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

        // set BorderLayout to split area
        setLayout(new BorderLayout());

        // Top area with meal history
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Meal to Swap from:"));
        topPanel.setPreferredSize(new Dimension(0, 200)); // This line is added for consistency

        // Wrap the history panel in a scroll pane
        JScrollPane historyScrollPane = new JScrollPane(mealHistoryPanel);
        historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        topPanel.add(historyScrollPane, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout());

        //middle1: Using FlowLayout instead of BorderLayout
        JPanel middleP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        middleP.setBorder(BorderFactory.createTitledBorder("Select the food to swap:"));

        middleP.add(new JLabel("Food Items:"));
        ingredientComboBox.setPreferredSize(new Dimension(200, 25));
        middleP.add(ingredientComboBox);

        //middle2
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Set Your Goal:"));

        // header panel for goal section
        JPanel goalHeaderPanel = new JPanel(new BorderLayout());

        JPanel goalButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addGoalButton = new JButton("+");
        removeGoalButton = new JButton("-");
        goalButtonPanel.add(addGoalButton);
        goalButtonPanel.add(removeGoalButton);
        goalHeaderPanel.add(goalButtonPanel, BorderLayout.CENTER);

        middlePanel.add(goalHeaderPanel, BorderLayout.NORTH);

        // wrap goalContainerPanel in a scroll pane with fixed size
        goalContainerPanel = new JPanel();
        goalContainerPanel.setLayout(new BoxLayout(goalContainerPanel, BoxLayout.Y_AXIS));

        JScrollPane goalScrollPane = new JScrollPane(goalContainerPanel);
        goalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        middlePanel.add(goalScrollPane, BorderLayout.CENTER);

        middle.add(middleP, BorderLayout.NORTH);
        middle.add(middlePanel, BorderLayout.CENTER);

        //bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelButton = new JButton("Cancel");
        bottomPanel.add(cancelButton);
        getReplaceButton = new JButton("Get Replaceable Food");
        bottomPanel.add(getReplaceButton);

        // add above sections
        add(topPanel, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setupGoalButton();
        addGoalRow();
    }

    // internal methods for setting goal(s)
    private JPanel createNewGoal() {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        // size constraints to prevent unbounded growth
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // First line for Nutrient and Action
        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JComboBox<String> nutrientList = new JComboBox<>(this.nutrientList);
        nutrientList.setPreferredSize(new Dimension(100, 25));

        JComboBox<String> actionList = new JComboBox<>(this.actionList);
        actionList.setPreferredSize(new Dimension(100, 25));
        
        topLine.add(new JLabel("Nutrient"));
        topLine.add(nutrientList);
        topLine.add(new JLabel("Action:"));
        topLine.add(actionList);
        
        // Second line for Intensities
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

    // setter methods for options (nutrient) getting from model
    public void setNutrientList(String[] nutrientList) {
    	this.nutrientList = nutrientList;
    	for (JComboBox<String> list : nutrientComboBox) {
    		list.setModel(new DefaultComboBoxModel<>(nutrientList));
    	}
    }

    public void setIngredientList(List<FoodItem> list) {
    	this.ingredientList = list;

    	// Clear existing items
        ingredientComboBox.removeAllItems();

        // Add each food name to the combo box
        for (FoodItem item : this.ingredientList) {
            ingredientComboBox.addItem(item.getName());
        }

    	revalidate();
    }

    public void setUnitsForRow(int rowIndex, String[] units) {
        if (rowIndex >= 0 && rowIndex < unitCombo.size()) {
            JComboBox<String> unitBox = unitCombo.get(rowIndex);
            String currentSelection = (String) unitBox.getSelectedItem();
            unitBox.removeAllItems();
            for (String unit : units) {
                unitBox.addItem(unit);
            }
            unitBox.setSelectedItem(currentSelection);
        }
    }

    public void onNutrientSelected(BiConsumer<Integer, String> action) {
        this.nutrientSelectionAction = action;
        for (int i = 0; i < nutrientComboBox.size(); i++) {
            applyNutrientListenerToRow(i);
        }
    }

    private void applyNutrientListenerToRow(int rowIndex) {
        JComboBox<String> nutrientBox = nutrientComboBox.get(rowIndex);

        for (ActionListener al : nutrientBox.getActionListeners()) {
            nutrientBox.removeActionListener(al);
        }

        nutrientBox.addActionListener(e -> {
            String selectedNutrient = (String) nutrientBox.getSelectedItem();
            if (selectedNutrient != null && !selectedNutrient.equals("<pick one>") && nutrientSelectionAction != null) {
                nutrientSelectionAction.accept(rowIndex, selectedNutrient);
            }
        });
    }

    // getter methods to be utilized by a facade
    public List<String> getSelectedNutrient() {
    	List<String> nutrient = new ArrayList<>();
    	for(JComboBox<String> list : nutrientComboBox)
    		nutrient.add((String) list.getSelectedItem());
    	return nutrient;
    }

    public List<String> getSelectedAction() {
    	List<String> action = new ArrayList<>();
    	for(JComboBox<String> list : actionComboBox)
    		action.add((String) list.getSelectedItem());
    	return action;
    }

    public List<String> getSelectedIntensityPrecise() {
    	List<String> intensity = new ArrayList<>();
    	for(JTextField list : preciseField)
    		intensity.add((String) list.getText());
    	return intensity;
    }

    public List<String> getSelectedUnit() {
    	List<String> unit = new ArrayList<>();
    	for(JComboBox<String> list : unitCombo)
    		unit.add((String) list.getSelectedItem());
    	return unit;
    }

    public MealHistoryPanel getMealHistorySelection() {
    	return this.forMealSelection;
    }

    public String getSelectedIngredient() {
    	return (String) ingredientComboBox.getSelectedItem();
    }

    // Action Listeners
    public void cancelButtonListener(ActionListener listener) {
    	cancelButton.addActionListener(listener);
    }

    public void getReplaceButtonListener(ActionListener listener) {
        getReplaceButton.addActionListener(listener);
    }

    private void setupGoalButton() {
    	addGoalButton.addActionListener(e -> addGoalRow());
    	removeGoalButton.addActionListener(e -> removeGoalRow());
    }

    private void updateButtonState() {
    	addGoalButton.setEnabled(nutrientComboBox.size() < MAX_OPTIONS);
    	removeGoalButton.setEnabled(nutrientComboBox.size() > MIN_OPTIONS);
    }
}