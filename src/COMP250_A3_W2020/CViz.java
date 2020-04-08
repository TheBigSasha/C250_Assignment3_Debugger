package COMP250_A3_W2020;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Stack;

public class CViz extends CatTree {                         //TODO: Cleanup console outputs
    //TODO: Implement cost at the top, autotesters and the other useful methods!
    //User Facing Parameters
    protected int wideningCoeff = 2;
    //Panels
    private JPanel GraphRegion;                             //JPanel on which the tree is drawn
    private JPanel MainWindow;                              //The entire window
    private JPanel MainPanel;                               //Right side of the interface with the tree and controls
    private JPanel ListOfCatsSide;                          //Left side of the interface with the list of cats
    //Scroll panes
    private JScrollPane catScroller;                        //Scroll pane around list of cats
    private JScrollPane GraphScroller;                      //Scroll pane around graph area
    //Interactive elements
    private JSlider RandomnessSlider;
    private JSlider WideningSlider;
    private JButton AddRandomCat;
    private JButton AddCustom;
    private JButton forceRefreshButton;
    private JButton RemoveRandom;                           //TODO: Implement this
    private JButton stressTestButton;
    private JSlider testIntensitySlider;
    private JRadioButton drawSamesRadioButton;
    private JRadioButton drawNamesRadioButton;
    private JRadioButton drawMonthHiredRadioButton;
    private JButton gradualTestButton;
    private JLabel CostPlanningDisplay;
    private JLabel CatsInTreeDisplay;
    private JLabel FluffiestDisplay;
    private JSpinner FluffiestFromMonthSpinner;
    private JLabel FluffiestFromMonthDisplay;
    private JSlider CostPlanningSlider;
    protected int randomnessCoeff = 2;
    //Randomization engine for assignment related objects
    private RandomCats rand = new RandomCats();             //Random cat generator

    //======================= User facing methods & constructors =========================

    /**
     * Instantiates a CViz object. Functional as a drop in replacement for a CatTree object.
     * <p>
     * Builds GUI and generates CatTree
     *
     * @param c any CatInfo to instantiate the root node.
     */
    public CViz(CatInfo c) {
        super(c);                                           //Calls constructor of superclass
        $$$setupUI$$$();
        addListeners();                                     //Binds listeners for UI
        refresh();                                          //Draws the graphics elements
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("CViz");
        RandomCats statRand = new RandomCats();
        frame.setContentPane(statRand.nextCViz().MainWindow);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //======================= UI Generation Methods =========================

    private void createUIComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        ListOfCatsSide = new JPanel();
        ListOfCatsSide.setLayout(new BoxLayout(ListOfCatsSide, BoxLayout.Y_AXIS));
        GraphRegion = new JPanel();
        GraphRegion.add(new GraphZone());
        //GraphRegion.add(new ShapesJPanel());
        rand = new RandomCats();
        //ListOfCatsSide.add(new CatNodeDrawing(rand.nextCatNode()));
        refresh();

    }


    private int sum(int[] input) {
        int output = 0;
        for (int i : input) {
            output += i;
        }
        return output;
    }

    private void displayNumbers() {
        try {
            CostPlanningDisplay.setText(displaySumArray(costPlanning(CostPlanningSlider.getValue())) + " over " + CostPlanningSlider.getValue() + " months.");
            CostPlanningDisplay.setToolTipText("No zeroes: " + displayArrayNoZero(costPlanning(CostPlanningSlider.getValue())));
            CostPlanningSlider.setToolTipText(displayArray(costPlanning(CostPlanningSlider.getValue())) + " over " + CostPlanningSlider.getValue() + " months.");
        } catch (Exception e) {
            CostPlanningDisplay.setText("Error");
        }
        try {
            FluffiestDisplay.setText(Integer.toString(fluffiest()));
        } catch (Exception e) {
            FluffiestDisplay.setText("Error");
        }
        try {
            FluffiestFromMonthDisplay.setText(fluffiestFromMonth((int) FluffiestFromMonthSpinner.getValue()).name + " with fur " + fluffiestFromMonth((int) FluffiestFromMonthSpinner.getValue()).furThickness);
        } catch (Exception e) {
            FluffiestFromMonthDisplay.setText("N/A");
        }
        try {
            ArrayList<CatInfo> list = new ArrayList<>();
            CatTreeIterator iter = new CatTreeIterator();
            for (CatTreeIterator it = iter; it.hasNext(); ) {
                CatInfo n = it.next();
                list.add(n);
            }
            int numberOfNodes = list.size();
            CatsInTreeDisplay.setText(Integer.toString(numberOfNodes));
        } catch (Exception e) {
            CatsInTreeDisplay.setText("Error");
        }
    }

    private String displaySumArray(int[] input) {
        return Integer.toString(sum(input));
    }

    private String displayArrayNoZero(int[] input) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (input[i] != 0) {
                s.append("at month ");
                s.append(i);
                s.append(" cost was ");
                s.append(input[i]);
                s.append(", ");
            }
        }
        s.replace(s.length() - 2, s.length(), "");
        return s.toString();
    }

    private String displayArray(int[] input) {
        StringBuilder s = new StringBuilder();
        for (int i : input) {
            s.append(i);
            s.append(", ");
        }
        s.replace(s.length() - 2, s.length(), "");
        return s.toString();
    }

    private void updateList() {
        Stack<CatNode> s = new Stack<>();
        CatNode curr = root;
        ListOfCatsSide.removeAll();


        // traverse the tree for side list inOrder
        while (curr != null || s.size() > 0) {
            try {
                if (curr.same != null) {
                    CatNode temp = curr.same;
                    while (temp != null) {
                        //s.push(temp);
                        try {
                            temp = temp.same;
                        } catch (NullPointerException e) {//TODO: is this even necessary
                            //System.out.println("    [CViz / Debug] " + "is this even necessary?");  //TODO: Cleanup console output
                        }
                    }
                }
            } catch (NullPointerException e) {
            }

            while (curr != null) {

                s.push(curr);
                curr = curr.senior;
            }
            curr = s.pop();
            //output.add(curr);
            ListOfCatsSide.add(new CatNodeDrawing(curr).panel);

            //System.out.print(curr.data + " ");

            curr = curr.junior;
        }
    }

    private void refresh() {
        this.GraphRegion.updateUI();
        updateList();
        GraphRegion.repaint();
        ListOfCatsSide.repaint();
        ListOfCatsSide.revalidate();
        try {
            displayNumbers();
        } catch (Exception e) {
        }
    }

    //======================= UI Listener Methods =========================

    private void addListeners() {
        CostPlanningSlider.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        });
        FluffiestFromMonthSpinner.addComponentListener(new ComponentAdapter() {
        });
        FluffiestFromMonthSpinner.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        });
        gradualTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(("    [CViz / Utility] " + gradualTest(testIntensitySlider.getValue())));
                StressTest dialog = new StressTest();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        drawMonthHiredRadioButton.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        });
        drawSamesRadioButton.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        });
        drawNamesRadioButton.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }
        });
        forceRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        RemoveRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("    [CViz / Utility] " + removeRandom());

            }
        });
        stressTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int iter = 0;
                int max = testIntensitySlider.getValue();
                while (iter < max) {
                    iter++;
                    try {
                        System.out.println("    [CViz / Utility] " + addCats());
                    } catch (NullPointerException ex) {
                        System.out.println("    [CViz / Caught Runtime Exception] NullPointerException");
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        System.out.println("    [CViz / Caught Runtime Exception] " + ex.getMessage());
                    }
                    try {
                        System.out.println("    [CViz / Utility] " + removeCats());
                    } catch (NullPointerException ex) {
                        System.out.println("    [CViz / Caught Runtime Exception] NullPointerException");
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        System.out.println("    [CViz / Caught Runtime Exception] " + ex.getMessage());
                    }
                }
                StressTest dialog = new StressTest();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        RandomnessSlider.addChangeListener(e -> randomnessSliderChanged());
        WideningSlider.addChangeListener(e -> wideningSliderChanged());

        AddRandomCat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCat(rand.nextCatInfo());
            }
        });

        AddCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCustomCat dialog = new AddCustomCat();
                dialog.pack();
                dialog.setVisible(true);
                while (dialog.info == null) {
                    if (dialog.cancelled) {
                        break;
                    }
                }
                if (!dialog.cancelled) {
                    addCat(dialog.info);
                }
            }
        });
    }

    private void wideningSliderChanged() {
        this.wideningCoeff = WideningSlider.getValue();
        refresh();
    }

    private void randomnessSliderChanged() {
        this.randomnessCoeff = RandomnessSlider.getValue();
        refresh();
    }

    //======================== Auto Tester Methods =======================
    private String removeRandom() {
        ArrayList<CatInfo> list = new ArrayList<>();
        CatTreeIterator iter = new CatTreeIterator();
        for (CatTreeIterator it = iter; it.hasNext(); ) {
            CatInfo n = it.next();
            list.add(n);
        }
        int numberOfNodesBefore = list.size();
        int whichToRemove = 0;
        if (list.size() > 0) {
            whichToRemove = rand.nextInt(list.size());
        } else {
            return "No nodes to remove from";
        }
        removeCat(list.get(whichToRemove));
        CatTreeIterator iter2 = new CatTreeIterator();
        int numberOfNodesAfter = 0;
        for (CatTreeIterator it = iter2; it.hasNext(); ) {
            CatInfo n = it.next();
            numberOfNodesAfter++;
        }
        if (numberOfNodesAfter != numberOfNodesBefore - 1) {
            return "Remove error? Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter + " cat removed was " + list.get(whichToRemove).name;
        } else {
            return "Random remove probably succeeded. Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        }
    }

    private String removeCats(int toExtermiante) {
        ArrayList<CatInfo> list = new ArrayList<>();
        CatTreeIterator iter = new CatTreeIterator();
        for (CatTreeIterator it = iter; it.hasNext(); ) {
            CatInfo n = it.next();
            list.add(n);
        }
        int numberOfNodesBefore = list.size();
        if (toExtermiante >= list.size()) toExtermiante = list.size() - 1;
        int numberRemoved = 0;
        int whichToRemove;
        while (list.size() != numberOfNodesBefore - toExtermiante) {
            whichToRemove = rand.nextInt(list.size());
            removeCat(list.get(whichToRemove));
        }
        CatTreeIterator iter2 = new CatTreeIterator();
        int numberOfNodesAfter = 0;
        for (CatTreeIterator it = iter2; it.hasNext(); ) {
            CatInfo n = it.next();
            numberOfNodesAfter++;
        }
        if (numberOfNodesAfter != numberOfNodesBefore - toExtermiante) {
            return "Remove error? Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        } else {
            return "Random remove probably succeeded. Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        }
    }

    public void delay(long pauseTimeMillis) {
    /*This function pauses code execution for an input amount of time in milliseconds.
    It exists only because delay() isn't a thing in java. */
        try {
            Thread.sleep(pauseTimeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String gradualTest(int maximum) {
        StringBuilder output = new StringBuilder("    [CViz / GradualTest] Test beginning with " + maximum + " stages." + "\n");
        root = rand.nextCatNode();
        for (int i = 1; i <= maximum; i++) {
            output.append("    [CViz / GradualTest] Reached stage " + i + "\n");
            for (int j = 1; j <= i; j++) {
                addCat(rand.nextCatInfo());
                /*if (maximum < 10) {
                    delay(150);
                }*/
                this.refresh();
            }
            for (int j = 1; j <= i; j++) {
                try {
                    output.append(("    [CViz / GradualTest] " + removeRandom()) + "\n");
                } catch (Exception e) {
                    output.append("    [CViz / GradualTest / Caught Runtime Exception] " + e.getCause() + "\n");
                    e.printStackTrace();
                }
                /*if (maximum < 10) {
                    delay(150);
                }*/
                this.refresh();
            }
            root = rand.nextCatNode();
        }
        output.append("    [CViz / GradualTest] Test concluded.");
        return output.toString();
    }

    private String removeCats() {
        ArrayList<CatInfo> list = new ArrayList<>();
        CatTreeIterator iter = new CatTreeIterator();
        for (CatTreeIterator it = iter; it.hasNext(); ) {
            CatInfo n = it.next();
            list.add(n);
        }
        int numberOfNodesBefore = list.size();
        int toExtermiante = rand.nextInt(list.size());
        int numberRemoved = 0;
        int whichToRemove;
        while (list.size() != numberOfNodesBefore - toExtermiante) {
            whichToRemove = rand.nextInt(list.size());
            removeCat(list.get(whichToRemove));
        }
        CatTreeIterator iter2 = new CatTreeIterator();
        int numberOfNodesAfter = 0;
        for (CatTreeIterator it = iter2; it.hasNext(); ) {
            CatInfo n = it.next();
            numberOfNodesAfter++;
        }
        if (numberOfNodesAfter != numberOfNodesBefore - toExtermiante) {
            return "Remove error? Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        } else {
            return "Random remove probably succeeded. Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        }
    }

    private String removeAllCats() {
        ArrayList<CatInfo> list = new ArrayList<>();
        CatTreeIterator iter = new CatTreeIterator();
        for (CatTreeIterator it = iter; it.hasNext(); ) {
            CatInfo n = it.next();
            list.add(n);
        }
        int numberOfNodesBefore = list.size();
        int toExtermiante = list.size() - 1;
        int numberRemoved = 0;
        int whichToRemove;
        while (list.size() != numberOfNodesBefore - toExtermiante) {
            whichToRemove = rand.nextInt(list.size());
            removeCat(list.get(whichToRemove));
        }
        CatTreeIterator iter2 = new CatTreeIterator();
        int numberOfNodesAfter = 0;
        for (CatTreeIterator it = iter2; it.hasNext(); ) {
            CatInfo n = it.next();
            numberOfNodesAfter++;
        }
        if (numberOfNodesAfter != numberOfNodesBefore - toExtermiante) {
            return "Remove error? Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        } else {
            return "Random remove probably succeeded. Number of cats before remove was " + numberOfNodesBefore + " after remove was " + numberOfNodesAfter;
        }
    }

    private String addCats() {
        int numToAdd = rand.nextInt(10);
        int iter = 0;
        while (iter < numToAdd) {
            iter++;
            addCat(rand.nextCatInfo());
        }
        return "added " + numToAdd + " cats to the tree";

    }

    //======================= Overridden methods =========================

    @Override
    public void addCat(CatInfo c) {
        if (root == null) {
            root = new CatNode(c);
        }
        super.addCat(c);
        refresh();
    }

    @Override
    public void removeCat(CatInfo c) {
        //System.out.println("    [CViz / Debug] " + "Remove called on cat with name " + c.name);
        super.removeCat(c);
        refresh();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        MainWindow = new JPanel();
        MainWindow.setLayout(new BorderLayout(0, 0));
        MainPanel = new JPanel();
        MainPanel.setLayout(new GridBagLayout());
        MainPanel.setAutoscrolls(true);
        MainPanel.setMaximumSize(new Dimension(5120, 2147483647));
        MainPanel.setMinimumSize(new Dimension(556, 415));
        MainPanel.setPreferredSize(new Dimension(1000, 415));
        MainWindow.add(MainPanel, BorderLayout.CENTER);
        GraphRegion.setMinimumSize(new Dimension(300, 200));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100.0;
        gbc.weighty = 10.0;
        gbc.fill = GridBagConstraints.BOTH;
        MainPanel.add(GraphRegion, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        MainPanel.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(panel2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel2.add(panel3);
        WideningSlider = new JSlider();
        WideningSlider.setMaximum(10);
        WideningSlider.setMinimum(1);
        WideningSlider.setPaintLabels(true);
        WideningSlider.setPaintTicks(true);
        WideningSlider.setSnapToTicks(false);
        WideningSlider.setToolTipText("Widening Coefficeint");
        WideningSlider.setValue(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(WideningSlider, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Widening Coefficent");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        panel3.add(label1, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel2.add(panel4);
        RandomnessSlider = new JSlider();
        RandomnessSlider.setMaximum(22);
        RandomnessSlider.setMinimum(1);
        RandomnessSlider.setValue(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(RandomnessSlider, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Randomness Coefficient");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        panel4.add(label2, gbc);
        forceRefreshButton = new JButton();
        forceRefreshButton.setText("Force Refresh");
        panel2.add(forceRefreshButton);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel5, gbc);
        drawSamesRadioButton = new JRadioButton();
        drawSamesRadioButton.setText("Draw Sames");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(drawSamesRadioButton, gbc);
        drawNamesRadioButton = new JRadioButton();
        drawNamesRadioButton.setText("Draw Names");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(drawNamesRadioButton, gbc);
        drawMonthHiredRadioButton = new JRadioButton();
        drawMonthHiredRadioButton.setSelected(true);
        drawMonthHiredRadioButton.setText("Draw Month Hired");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(drawMonthHiredRadioButton, gbc);
        catScroller = new JScrollPane();
        catScroller.setHorizontalScrollBarPolicy(30);
        catScroller.setMaximumSize(new Dimension(400, 32767));
        catScroller.setMinimumSize(new Dimension(300, 39));
        catScroller.setPreferredSize(new Dimension(350, 10));
        catScroller.setVerticalScrollBarPolicy(22);
        MainWindow.add(catScroller, BorderLayout.WEST);
        catScroller.setBorder(BorderFactory.createTitledBorder(null, "Cats in Order", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, catScroller.getFont())));
        catScroller.setViewportView(ListOfCatsSide);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        MainWindow.add(panel6, BorderLayout.EAST);
        stressTestButton = new JButton();
        stressTestButton.setText("Stress Test");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel6.add(stressTestButton, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel6.add(panel7, gbc);
        final JLabel label3 = new JLabel();
        label3.setFocusTraversalPolicyProvider(false);
        label3.setHorizontalAlignment(0);
        label3.setHorizontalTextPosition(0);
        label3.setText("Test Constant");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel7.add(label3, gbc);
        testIntensitySlider = new JSlider();
        testIntensitySlider.setMaximum(30);
        testIntensitySlider.setMinimum(1);
        testIntensitySlider.setName("Test Intensity");
        testIntensitySlider.setOrientation(1);
        testIntensitySlider.setPaintLabels(true);
        testIntensitySlider.setPaintTicks(true);
        testIntensitySlider.setToolTipText("Test Intensity");
        testIntensitySlider.setValue(10);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel7.add(testIntensitySlider, gbc);
        AddRandomCat = new JButton();
        AddRandomCat.setText("Add Random Cat");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel6.add(AddRandomCat, gbc);
        RemoveRandom = new JButton();
        RemoveRandom.setText("Rem Random Cat");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel6.add(RemoveRandom, gbc);
        AddCustom = new JButton();
        AddCustom.setText("Add Custom Cat");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel6.add(AddCustom, gbc);
        gradualTestButton = new JButton();
        gradualTestButton.setText("Gradual Test");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(gradualTestButton, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel8, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(panel9, gbc);
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 22, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Cost Planning");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(label4, gbc);
        CostPlanningDisplay = new JLabel();
        CostPlanningDisplay.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel9.add(CostPlanningDisplay, gbc);
        CostPlanningSlider = new JSlider();
        CostPlanningSlider.setMaximum(365);
        CostPlanningSlider.setMinimum(1);
        CostPlanningSlider.setToolTipText("NumberofMonths");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(CostPlanningSlider, gbc);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(panel10, gbc);
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, -1, 22, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Cats in Tree");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(label5, gbc);
        CatsInTreeDisplay = new JLabel();
        CatsInTreeDisplay.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel10.add(CatsInTreeDisplay, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(panel11, gbc);
        final JLabel label6 = new JLabel();
        Font label6Font = this.$$$getFont$$$(null, -1, 22, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        label6.setHorizontalAlignment(2);
        label6.setHorizontalTextPosition(2);
        label6.setText("Fluffiest");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(label6, gbc);
        FluffiestDisplay = new JLabel();
        FluffiestDisplay.setText("0");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(FluffiestDisplay, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(panel12, gbc);
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, -1, 22, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("Fluffiest From Month");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(label7, gbc);
        FluffiestFromMonthSpinner = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel12.add(FluffiestFromMonthSpinner, gbc);
        FluffiestFromMonthDisplay = new JLabel();
        FluffiestFromMonthDisplay.setText("None");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(FluffiestFromMonthDisplay, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel12.add(spacer1, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainWindow;
    }

    //======================= UI Element Nested Classes =========================

    class GraphZone extends JPanel {                    //Responsible for drawing the binary tree

        public GraphZone() {
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public Dimension getPreferredSize() {
            Dimension idealSize = new Dimension(1280, 800);
            //System.out.println("    [CViz / Debug] " + idealSize);
            return idealSize;      //TODO: Dynamic sizing
        }

        public void paintComponent(Graphics g) {
            //Draw the tree
            super.paintComponent(g);
            int[] start = {this.getWidth() / 2, 20};
            if (root != null) {
                drawNodeWithChildren(g, start, root);
            } else {
                g.drawString("root is null; tree is empty", this.getWidth() / 2, this.getHeight() / 2);
                g.drawString("root is null; tree is empty", this.getWidth() / 2, 20);
            }

            //Attempt auto resize //TODO: check this implementation
            this.setPreferredSize(this.getPreferredSize());

            //Attempt to automatically scroll to the middle of the graphics region
            try {
                GraphScroller.getHorizontalScrollBar().setValue(GraphRegion.getWidth() / 3);
            } catch (Exception e) {
                try {
                    GraphScroller.getHorizontalScrollBar().setValue(GraphScroller.getWidth() / 2);
                } catch (Exception f) {
                    try {
                        GraphScroller.getHorizontalScrollBar().setValue(600);
                    } catch (Exception n) {
                        //System.out.println("    [CViz / Debug] " + "not resized");
                    }
                }
            }

        }

        private void drawNodeWithChildren(Graphics g, int[] coords, CatNode node) {
            //System.out.println("    [CViz / Debug] " + "drawing node " + node + " at coords " + coords[0] + ", " + coords[1]);
            StringBuilder title = new StringBuilder();
            if (drawMonthHiredRadioButton.isSelected()) {
                title.append(node.data.monthHired);
            }
            if (drawNamesRadioButton.isSelected()) {
                title.append(" ").append(node.data.name);
            }
            if (drawSamesRadioButton.isSelected()) {
                if (node.same != null) {
                    CatNode temp = node;
                    int iter = 1;
                    try {
                        while (temp.same != null) {
                            temp = temp.same;
                            g.setColor(Color.DARK_GRAY);
                            g.drawLine(coords[0], coords[1], coords[0], coords[1] + (20 * iter));
                            StringBuilder titleSame = new StringBuilder();
                            if (drawMonthHiredRadioButton.isSelected()) {
                                titleSame.append(temp.data.monthHired);
                            }
                            if (drawNamesRadioButton.isSelected()) {
                                titleSame.append(" ").append(temp.data.name);
                            }
                            g.drawRect(coords[0], coords[1] + (20 * iter), titleSame.toString().length() * 7, 20);
                            g.drawString(titleSame.toString(), coords[0] + 2, coords[1] + (20 * iter) + 15);
                            iter++;
                        }
                        g.setColor(Color.BLACK);
                    } catch (Exception e) {
                    }
                }
            }
            g.drawRect(coords[0], coords[1], title.toString().length() * 7, 20);
            g.drawString(title.toString(), coords[0] + 2, coords[1] + 15);
            if (node.senior != null) {
                g.setColor(Color.BLUE);
                //System.out.println("    [CViz / Debug] " + "recursing to senior");
                int[] nodeSenCoords = {coords[0] - ((35 + (int) Math.round(Math.sqrt(coords[1])) * wideningCoeff + (int) Math.round(Math.pow(randomnessCoeff / 2.0 - rand.nextInt(Math.abs(randomnessCoeff)), 2)) + (3 - rand.nextInt(3 * randomnessCoeff)))), coords[1] + (50 + (7 - rand.nextInt(14)))};
                g.drawLine(coords[0], coords[1], nodeSenCoords[0], nodeSenCoords[1]);
                drawNodeWithChildren(g, nodeSenCoords, node.senior);
            }
            if (node.junior != null) {
                g.setColor(Color.DARK_GRAY);
                //System.out.println("    [CViz / Debug] " + "recursing to junior");
                int[] nodeJunCoords = {coords[0] + ((35 + (int) Math.round(Math.sqrt(coords[1])) * wideningCoeff + (int) Math.round(Math.pow(randomnessCoeff / 2.0 - rand.nextInt(Math.abs(randomnessCoeff)), 2)) + (3 - rand.nextInt(3 * randomnessCoeff)))), coords[1] + (50 + (7 - rand.nextInt(14)))};
                g.drawLine(coords[0], coords[1], nodeJunCoords[0], nodeJunCoords[1]);
                drawNodeWithChildren(g, nodeJunCoords, node.junior);
            }
        }
    }

    class CatNodeDrawing extends JComponent {
        //private static RandomCats rand;
        protected JPanel panel;
        protected JScrollPane scrollPane;
        private ArrayList<CatBox> CatBoxList = new ArrayList<>();
        private Dimension idealSize;
        private CatNode node;
        private Dimension size = new Dimension(20, 20);
        private boolean isList = false;
        private int listLength = 0;
        private ArrayList<CatNode> catsList = new ArrayList<>();
        private JPanel CatsList;
        private JPanel innerPanel;
        private JTextArea CatsOfMonth;

        public CatNodeDrawing(CatNode input) {
            this.node = input;
            $$$setupUI$$$();
            this.catsList.add(this.node);
            if (this.node.same != null) {
                this.isList = true;
                CatNode temp = this.node;
                while (temp.same != null) {
                    listLength++;
                    temp = temp.same;
                    catsList.add(temp);
                }
            }
            this.idealSize = new Dimension((30 + (node.data.name.length() * 3 + (9 * Integer.toString(node.data.expectedGroomingCost).length()))), (20 + this.listLength * 20));

            //System.out.println("    [CViz / Debug] " + "Size of catnode visualizer is " + this.idealSize);

            panel.setSize(idealSize);
            this.setPreferredSize(idealSize);
            this.setSize(idealSize);
            this.setMinimumSize(idealSize);

            panel.setBackground(Color.GRAY);

            if (catsList.size() > 1) {
                //innerPanel.add(new JLabel("    [CViz / Debug] " + "Cats hired on " + node.data.monthHired));
                CatsOfMonth.setEnabled(true);
                CatsOfMonth.setVisible(true);
                CatsOfMonth.setText("Cats hired on month " + node.data.monthHired);
            }

            for (CatNode cat : catsList) {
                //System.out.println("    [CViz / Debug] " + new CatBox(cat.data).getSize());
                CatBoxList.add(new CatBox(cat.data));
            }

            for (CatBox box : CatBoxList) {
                CatsList.add(box.mainPanel);
            }

            add(panel);

            this.setBackground(Color.gray);
        }

        /*public static void main(String[] args) {
            rand = new RandomCats();
            JFrame frame = new JFrame("CatNodeDrawing");
            CatTree.CatNode testCat = rand.nextCatNode();
            testCat.same = rand.nextCatNode();
            frame.setContentPane(new CatNodeDrawing(testCat).panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }*/

        private void createUIComponents() {
            CatsList = new JPanel();
            CatsList.setLayout(new BoxLayout(this.CatsList, BoxLayout.Y_AXIS));

        }

        /**
         * Method generated by IntelliJ IDEA GUI Designer
         * >>> IMPORTANT!! <<<
         * DO NOT edit this method OR call it in your code!
         *
         * @noinspection ALL
         */
        private void $$$setupUI$$$() {
            createUIComponents();
            panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            innerPanel = new JPanel();
            innerPanel.setLayout(new BorderLayout(0, 0));
            panel.add(innerPanel);
            scrollPane = new JScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(31);
            innerPanel.add(scrollPane, BorderLayout.SOUTH);
            scrollPane.setViewportView(CatsList);
            CatsOfMonth = new JTextArea();
            CatsOfMonth.setEditable(false);
            CatsOfMonth.setEnabled(false);
            CatsOfMonth.setFocusable(false);
            Font CatsOfMonthFont = this.$$$getFont$$$(null, -1, 18, CatsOfMonth.getFont());
            if (CatsOfMonthFont != null) CatsOfMonth.setFont(CatsOfMonthFont);
            CatsOfMonth.setLineWrap(true);
            CatsOfMonth.setOpaque(true);
            CatsOfMonth.setVisible(false);
            innerPanel.add(CatsOfMonth, BorderLayout.NORTH);
        }

        /**
         * @noinspection ALL
         */
        private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
            if (currentFont == null) return null;
            String resultName;
            if (fontName == null) {
                resultName = currentFont.getName();
            } else {
                Font testFont = new Font(fontName, Font.PLAIN, 10);
                if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                    resultName = fontName;
                } else {
                    resultName = currentFont.getName();
                }
            }
            return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        }

        /**
         * @noinspection ALL
         */
        public JComponent $$$getRootComponent$$$() {
            return panel;
        }

        public class CatBox extends JComponent {
            protected JPanel mainPanel;
            protected CatInfo data;
            private JPanel UpperPanel;
            private JPanel LowerPanel;
            private JTextArea BigText;
            private JButton removeButton;
            private JPanel removeBtn;

            public CatBox() {
                data = new RandomCats().nextCatInfo();
                BigText.setText(data.name);
                LowerPanel.add(new JLabel("Hired " + data.monthHired));
                LowerPanel.add(new JLabel("Fur " + data.furThickness));
                LowerPanel.add(new JLabel("Next App. " + data.nextGroomingAppointment));
                LowerPanel.add(new JLabel("Cost " + data.expectedGroomingCost));
                this.setSize(70, 30);
                addListeners();

            }

            public CatBox(CatInfo data) {
                if (data == null) {
                    return;
                }
                this.data = data;
                BigText.setText(data.name);
                LowerPanel.add(new JLabel("Hired " + data.monthHired));
                LowerPanel.add(new JLabel("Fur " + data.furThickness));
                LowerPanel.add(new JLabel("Next App. " + data.nextGroomingAppointment));
                LowerPanel.add(new JLabel("Cost " + data.expectedGroomingCost));
                this.setSize(70, 30);
                addListeners();
            }

            /* public static void main(String[] args) {
                 JFrame frame = new JFrame("CatBox");
                 frame.setContentPane(new CatBox().mainPanel);
                 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 frame.pack();
                 frame.setVisible(true);
             }
        */
            private void addListeners() {
                removeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeCat(data);
                    }
                });
            }


            {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
                $$$setupUI$$$();
            }

            /**
             * Method generated by IntelliJ IDEA GUI Designer
             * >>> IMPORTANT!! <<<
             * DO NOT edit this method OR call it in your code!
             *
             * @noinspection ALL
             */
            private void $$$setupUI$$$() {
                final JPanel panel1 = new JPanel();
                panel1.setLayout(new GridBagLayout());
                mainPanel = new JPanel();
                mainPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc;
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                panel1.add(mainPanel, gbc);
                final JScrollPane scrollPane1 = new JScrollPane();
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                mainPanel.add(scrollPane1, gbc);
                LowerPanel = new JPanel();
                LowerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
                Font LowerPanelFont = this.$$$getFont$$$(null, -1, 12, LowerPanel.getFont());
                if (LowerPanelFont != null) LowerPanel.setFont(LowerPanelFont);
                scrollPane1.setViewportView(LowerPanel);
                removeBtn = new JPanel();
                removeBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.BOTH;
                mainPanel.add(removeBtn, gbc);
                BigText = new JTextArea();
                BigText.setEditable(false);
                BigText.setEnabled(true);
                Font BigTextFont = this.$$$getFont$$$(null, -1, 20, BigText.getFont());
                if (BigTextFont != null) BigText.setFont(BigTextFont);
                BigText.setOpaque(false);
                BigText.setWrapStyleWord(true);
                removeBtn.add(BigText);
                removeButton = new JButton();
                removeButton.setText("remove");
                removeBtn.add(removeButton);
            }

            /**
             * @noinspection ALL
             */
            private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
                if (currentFont == null) return null;
                String resultName;
                if (fontName == null) {
                    resultName = currentFont.getName();
                } else {
                    Font testFont = new Font(fontName, Font.PLAIN, 10);
                    if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                        resultName = fontName;
                    } else {
                        resultName = currentFont.getName();
                    }
                }
                return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
            }

        }
    }

    class AddCustomCat extends JDialog {
        private JPanel contentPane;
        private JButton buttonOK;
        private JButton buttonCancel;
        private JTextField enterNameTextField;
        private JSlider sliderFur, sliderCost, sliderMonth, sliderApp;
        private JSpinner spinnerFur, spinnerCost, spinnerApp, spinnerMonth;
        private JButton randomNameButton;
        private RandomCats rand = new RandomCats();
        public CatInfo info;
        public boolean cancelled = false;

        private int monthHired = 243;
        private int furThick = 100;
        private int nextApp = 100;
        private int nextAppCost = 0;
        private String name = "";

        public AddCustomCat() {
            setContentPane(contentPane);
            setModal(true);
            getRootPane().setDefaultButton(buttonOK);
            addListeners();


        }

        private void onOK() {
            // add your code here
            monthHired = sliderMonth.getValue();
            nextApp = sliderApp.getValue();
            nextAppCost = sliderCost.getValue();
            furThick = sliderFur.getValue();
            name = enterNameTextField.getText();
            info = new CatInfo(name, monthHired, furThick, nextApp, nextAppCost);
            //System.out.println("    [CViz / Debug] " + info);
            //addCat(new CatInfo(name, monthHired, furThick, nextApp, nextAppCost));
            dispose();
        }

        private void onCancel() {
            // add your code here if necessary
            info = null;
            cancelled = true;
            dispose();
        }

       /* public static void main(String[] args) {
            AddCustomCat dialog = new AddCustomCat();
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }*/

        private void addListeners() {
            sliderFur.addChangeListener(e -> sliderThickChanged());
            spinnerFur.addChangeListener(e -> spinnerThickChanged());
            sliderApp.addChangeListener(e -> sliderAppChanged());
            spinnerApp.addChangeListener(e -> spinnerAppChanged());
            sliderCost.addChangeListener(e -> sliderCostChanged());
            spinnerCost.addChangeListener(e -> spinnerCostChanged());
            sliderMonth.addChangeListener(e -> sliderMonthChanged());
            spinnerMonth.addChangeListener(e -> spinnerMonthChanged());
            refresh();

            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onOK();
                }
            });

            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            });

            // call onCancel() when cross is clicked
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    onCancel();
                }
            });

            // call onCancel() on ESCAPE
            contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            randomNameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enterNameTextField.setText(rand.nextName());
                }
            });
        }

        private void refresh() {
            sliderMonthChanged();
            sliderThickChanged();
            sliderCostChanged();
            sliderAppChanged();
            spinnerCostChanged();
            spinnerAppChanged();
            spinnerThickChanged();
            spinnerMonthChanged();
        }

        private void sliderThickChanged() {
            spinnerFur.setValue(sliderFur.getValue());
        }

        private void sliderMonthChanged() {
            spinnerMonth.setValue(sliderMonth.getValue());
        }

        private void sliderAppChanged() {
            spinnerApp.setValue(sliderApp.getValue());
        }

        private void sliderCostChanged() {
            spinnerCost.setValue(sliderCost.getValue());
        }

        private void spinnerMonthChanged() {
            sliderMonth.setValue(Integer.parseInt(spinnerMonth.getValue().toString()));
        }

        private void spinnerThickChanged() {
            sliderFur.setValue(Integer.parseInt(spinnerFur.getValue().toString()));
        }

        private void spinnerCostChanged() {
            sliderCost.setValue(Integer.parseInt(spinnerCost.getValue().toString()));
        }

        private void spinnerAppChanged() {
            sliderApp.setValue(Integer.parseInt(spinnerApp.getValue().toString()));
        }

        {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
            $$$setupUI$$$();
        }

        /**
         * Method generated by IntelliJ IDEA GUI Designer
         * >>> IMPORTANT!! <<<
         * DO NOT edit this method OR call it in your code!
         *
         * @noinspection ALL
         */
        private void $$$setupUI$$$() {
            contentPane = new JPanel();
            contentPane.setLayout(new GridBagLayout());
            final JPanel panel1 = new JPanel();
            panel1.setLayout(new BorderLayout(0, 0));
            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            contentPane.add(panel1, gbc);
            final JLabel label1 = new JLabel();
            label1.setText("Cat Wizard - <3 sashaphoto.ca");
            panel1.add(label1, BorderLayout.WEST);
            final JPanel panel2 = new JPanel();
            panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel1.add(panel2, BorderLayout.EAST);
            buttonOK = new JButton();
            buttonOK.setText("OK");
            panel2.add(buttonOK);
            buttonCancel = new JButton();
            buttonCancel.setText("Cancel");
            panel2.add(buttonCancel);
            final JPanel panel3 = new JPanel();
            panel3.setLayout(new BorderLayout(0, 0));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            contentPane.add(panel3, gbc);
            final JPanel panel4 = new JPanel();
            panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel3.add(panel4, BorderLayout.NORTH);
            enterNameTextField = new JTextField();
            enterNameTextField.setHorizontalAlignment(0);
            enterNameTextField.setMinimumSize(new Dimension(140, 30));
            enterNameTextField.setPreferredSize(new Dimension(140, 30));
            enterNameTextField.setText("Enter Name");
            enterNameTextField.setToolTipText("Enter Name");
            panel4.add(enterNameTextField);
            randomNameButton = new JButton();
            randomNameButton.setText("Random Name");
            panel4.add(randomNameButton);
            final JPanel panel5 = new JPanel();
            panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel3.add(panel5, BorderLayout.CENTER);
            final JPanel panel6 = new JPanel();
            panel6.setLayout(new GridBagLayout());
            panel5.add(panel6);
            sliderFur = new JSlider();
            sliderFur.setToolTipText("Fur Thickness");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            panel6.add(sliderFur, gbc);
            final JLabel label2 = new JLabel();
            label2.setText("Fur Thickness");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            panel6.add(label2, gbc);
            spinnerFur = new JSpinner();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel6.add(spinnerFur, gbc);
            final JPanel panel7 = new JPanel();
            panel7.setLayout(new GridBagLayout());
            panel5.add(panel7);
            sliderCost = new JSlider();
            sliderCost.setToolTipText("Estimated Grooming Cost");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            panel7.add(sliderCost, gbc);
            final JLabel label3 = new JLabel();
            label3.setText("Grooming Cost");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            panel7.add(label3, gbc);
            spinnerCost = new JSpinner();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel7.add(spinnerCost, gbc);
            final JPanel panel8 = new JPanel();
            panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            contentPane.add(panel8, gbc);
            final JPanel panel9 = new JPanel();
            panel9.setLayout(new GridBagLayout());
            panel8.add(panel9);
            sliderMonth = new JSlider();
            sliderMonth.setMaximum(243);
            sliderMonth.setMinimum(0);
            sliderMonth.setToolTipText("Month Hired");
            sliderMonth.setValue(122);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            panel9.add(sliderMonth, gbc);
            final JLabel label4 = new JLabel();
            label4.setText("Month Hired");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            panel9.add(label4, gbc);
            spinnerMonth = new JSpinner();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel9.add(spinnerMonth, gbc);
            final JPanel panel10 = new JPanel();
            panel10.setLayout(new GridBagLayout());
            panel8.add(panel10);
            sliderApp = new JSlider();
            sliderApp.setToolTipText("Next Appointment");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            panel10.add(sliderApp, gbc);
            final JLabel label5 = new JLabel();
            label5.setText("Next Appointment");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            panel10.add(label5, gbc);
            spinnerApp = new JSpinner();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel10.add(spinnerApp, gbc);
        }

        /**
         * @noinspection ALL
         */
        public JComponent $$$getRootComponent$$$() {
            return contentPane;
        }

    }

    class StressTest extends JDialog {
        private JPanel contentPane;
        private JButton buttonOK;

        public StressTest() {
            setContentPane(contentPane);
            setModal(true);
            getRootPane().setDefaultButton(buttonOK);

            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onOK();
                }
            });
        }

        private void onOK() {
            // add your code here
            dispose();
        }

        {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
            $$$setupUI$$$();
        }

        /**
         * Method generated by IntelliJ IDEA GUI Designer
         * >>> IMPORTANT!! <<<
         * DO NOT edit this method OR call it in your code!
         *
         * @noinspection ALL
         */
        private void $$$setupUI$$$() {
            contentPane = new JPanel();
            contentPane.setLayout(new GridBagLayout());
            final JPanel panel1 = new JPanel();
            panel1.setLayout(new GridBagLayout());
            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            contentPane.add(panel1, gbc);
            final JPanel panel2 = new JPanel();
            panel2.setLayout(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            panel1.add(panel2, gbc);
            buttonOK = new JButton();
            buttonOK.setText("OK");
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(buttonOK, gbc);
            final JPanel panel3 = new JPanel();
            panel3.setLayout(new BorderLayout(0, 0));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            contentPane.add(panel3, gbc);
            final JLabel label1 = new JLabel();
            label1.setText("View the Java console to see how you fared.");
            panel3.add(label1, BorderLayout.NORTH);
            final JLabel label2 = new JLabel();
            label2.setText("If the list disappears or the program freezes, adding a random cat (can replace root) is likely to help.");
            panel3.add(label2, BorderLayout.SOUTH);
            final JLabel label3 = new JLabel();
            label3.setText("Failure messages are generated by counting using the iterator. If your iterator does not work, you may ignore them.");
            panel3.add(label3, BorderLayout.WEST);
        }

        /**
         * @noinspection ALL
         */
        public JComponent $$$getRootComponent$$$() {
            return contentPane;
        }

    }

}