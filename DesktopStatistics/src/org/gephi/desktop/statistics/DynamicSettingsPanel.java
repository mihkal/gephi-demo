/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.desktop.statistics;

import java.util.concurrent.TimeUnit;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.gephi.statistics.spi.DynamicStatistics;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class DynamicSettingsPanel extends javax.swing.JPanel {

    public DynamicSettingsPanel() {
        initComponents();
    }
    Interval bounds = null;

    public void setup(DynamicStatistics dynamicStatistics) {
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        DynamicModel model = dynamicController.getModel();
        TimeInterval visibleInterval = model.getVisibleInterval();

        //Bounds
        bounds = dynamicStatistics.getBounds();
        if (bounds == null) {
            double low = visibleInterval.getLow();
            if (Double.isInfinite(low)) {
                low = model.getMin();
            }
            double high = visibleInterval.getHigh();
            if (Double.isInfinite(high)) {
                high = model.getMax();
            }
            bounds = new Interval(low, high);
        }
        String boundsStr = "";
        if (model.getTimeFormat().equals(DynamicModel.TimeFormat.DOUBLE)) {
            boundsStr = bounds.getLow() + " - " + bounds.getHigh();
        } else {
            boundsStr = DynamicUtilities.getXMLDateStringFromDouble(bounds.getLow()) + " - " + DynamicUtilities.getXMLDateStringFromDouble(bounds.getHigh());
        }
        currentIntervalLabel.setText(boundsStr);

        //Window and tick
        windowTextField.setText(dynamicStatistics.getWindow() + "");
        tickTextField.setText(dynamicStatistics.getWindow() + "");

        //TimeUnit
        if (model.getTimeFormat().equals(DynamicModel.TimeFormat.DOUBLE)) {
            windowTimeUnitCombo.setVisible(false);
            tickTimeUnitCombo.setVisible(false);
        }
    }

    public void unsetup(DynamicStatistics dynamicStatistics) {
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        DynamicModel model = dynamicController.getModel();

        //Bounds is the same
        dynamicStatistics.setBounds(bounds);

        //Window
        double window = 0.;
        if (model.getTimeFormat().equals(DynamicModel.TimeFormat.DOUBLE)) {
            window = Double.parseDouble(windowTextField.getText());
        } else {
            TimeUnit timeUnit = getSelectedTimeUnit(windowTimeUnitCombo.getModel());
            window = getTimeInMilliseconds(windowTextField.getText(), timeUnit);
        }
        dynamicStatistics.setWindow(window);

        //Tick
        double tick = 0.;
        if (model.getTimeFormat().equals(DynamicModel.TimeFormat.DOUBLE)) {
            tick = Double.parseDouble(tickTextField.getText());
        } else {
            TimeUnit timeUnit = getSelectedTimeUnit(tickTimeUnitCombo.getModel());
            window = getTimeInMilliseconds(tickTextField.getText(), timeUnit);
        }
        dynamicStatistics.setTick(tick);
    }

    public void createValidation(ValidationGroup group) {
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        DynamicModel model = dynamicController.getModel();
        if (model.getTimeFormat().equals(DynamicModel.TimeFormat.DOUBLE)) {
            group.add(windowTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                    Validators.numberRange(Double.MIN_VALUE, (bounds.getHigh() - bounds.getLow())));
            group.add(tickTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                    Validators.numberRange(Double.MIN_VALUE, (bounds.getHigh() - bounds.getLow())));
        } else {
            //TODO validation with dates
            group.add(windowTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                    new PositiveNumberValidator());
            group.add(tickTextField, Validators.REQUIRE_NON_EMPTY_STRING,
                    new PositiveNumberValidator());
        }
    }
    private final String DAYS = NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.TimeUnit.DAYS");
    private final String HOURS = NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.TimeUnit.HOURS");
    private final String MILLISECONDS = NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.TimeUnit.MILLISECONDS");
    private final String MINUTES = NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.TimeUnit.MINUTES");
    private final String SECONDS = NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.TimeUnit.SECONDS");

    private ComboBoxModel getTimeUnitModel() {
        return new DefaultComboBoxModel(new String[]{DAYS, HOURS, MILLISECONDS, MINUTES, SECONDS});
    }

    private TimeUnit getSelectedTimeUnit(ComboBoxModel comboBoxModel) {
        if (comboBoxModel.getSelectedItem().equals(DAYS)) {
            return TimeUnit.DAYS;
        } else if (comboBoxModel.getSelectedItem().equals(HOURS)) {
            return TimeUnit.HOURS;
        } else if (comboBoxModel.getSelectedItem().equals(MILLISECONDS)) {
            return TimeUnit.MILLISECONDS;
        } else if (comboBoxModel.getSelectedItem().equals(MINUTES)) {
            return TimeUnit.MINUTES;
        } else if (comboBoxModel.getSelectedItem().equals(SECONDS)) {
            return TimeUnit.SECONDS;
        }
        return null;
    }

    public double getTimeInMilliseconds(String text, TimeUnit timeUnit) {
        Integer t = Integer.parseInt(text);
        return TimeUnit.MILLISECONDS.convert(t, timeUnit);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelCurrentTimeline = new javax.swing.JLabel();
        currentIntervalLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        windowTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tickTextField = new javax.swing.JTextField();
        windowTimeUnitCombo = new javax.swing.JComboBox();
        tickTimeUnitCombo = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();

        labelCurrentTimeline.setText(org.openide.util.NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.labelCurrentTimeline.text")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.jLabel1.text")); // NOI18N

        windowTextField.setName("window"); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(DynamicSettingsPanel.class, "DynamicSettingsPanel.jLabel2.text")); // NOI18N

        tickTextField.setName("tick"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(labelCurrentTimeline)
                        .add(18, 18, 18)
                        .add(currentIntervalLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(tickTextField)
                            .add(windowTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tickTimeUnitCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(windowTimeUnitCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelCurrentTimeline)
                    .add(currentIntervalLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(windowTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(windowTimeUnitCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(tickTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tickTimeUnitCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentIntervalLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelCurrentTimeline;
    private javax.swing.JTextField tickTextField;
    private javax.swing.JComboBox tickTimeUnitCombo;
    private javax.swing.JTextField windowTextField;
    private javax.swing.JComboBox windowTimeUnitCombo;
    // End of variables declaration//GEN-END:variables

    public static JPanel createCounpoundPanel(DynamicSettingsPanel dynamicPanel, JPanel innerPanel) {
        JPanel result = new JPanel();

        java.awt.GridBagConstraints gridBagConstraints;

        result.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        result.add(dynamicPanel, gridBagConstraints);

        if (innerPanel != null) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            result.add(innerPanel, gridBagConstraints);
        }

        //Validation
        ValidationPanel validationPanel = new ValidationPanel();
        validationPanel.setInnerComponent(result);
        ValidationGroup group = validationPanel.getValidationGroup();
        dynamicPanel.createValidation(group);

        return validationPanel;
    }
}