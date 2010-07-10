/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StreamingServerPanel.java
 *
 * Created on Jun 13, 2010, 6:45:46 PM
 */

package org.gephi.desktop.streaming;

import org.openide.util.Lookup;

/**
 *
 * @author panisson
 */
public class StreamingServerPanel extends javax.swing.JPanel {

    private StreamingController controller;

    /** Creates new form StreamingServerPanel */
    public StreamingServerPanel() {
        initComponents();
        controller = Lookup.getDefault().lookup(StreamingController.class);
        controller.setServerPanel(this);
        this.refreshModel();
    }

    public final void refreshModel() {
        StreamingModel model = controller.getStreamingModel();

        if (model == null) {
            startStopButton.setEnabled(false);
            
        } else {
            startStopButton.setEnabled(true);

            if (model.isServerRunning()) {
                startStopButton.setText("Stop");

                StringBuilder labelText = new StringBuilder("Streaming Server listening at<br>");
                //TODO: test if http or https and listening port
                String url = "http://localhost:8080"+model.getServerContext();
    //            labelText.append("<a href=\"")
    //                    .append(url)
    //                    .append("\"")
    //                    .append(url)
    //                    .append("</a>");
                labelText.append(url);

                streamingUrlTextPane.setText(labelText.toString());
            } else {
                startStopButton.setText(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.startStopButton.text"));
                streamingUrlTextPane.setText("");
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startStopButton = new javax.swing.JButton();
        configureButton = new javax.swing.JButton();
        streamingUrlScrollPane = new javax.swing.JScrollPane();
        streamingUrlTextPane = new javax.swing.JTextPane();

        setPreferredSize(new java.awt.Dimension(400, 131));

        startStopButton.setText(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.startStopButton.text")); // NOI18N
        startStopButton.setToolTipText(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.startStopButton.toolTipText")); // NOI18N
        startStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopButtonActionPerformed(evt);
            }
        });

        configureButton.setText(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.configureButton.text")); // NOI18N
        configureButton.setToolTipText(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.configureButton.toolTipText")); // NOI18N

        streamingUrlScrollPane.setBorder(null);
        streamingUrlScrollPane.setViewportBorder(null);

        streamingUrlTextPane.setBorder(null);
        streamingUrlTextPane.setContentType(org.openide.util.NbBundle.getMessage(StreamingServerPanel.class, "StreamingServerPanel.streamingUrlTextPane.contentType")); // NOI18N
        streamingUrlScrollPane.setViewportView(streamingUrlTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(streamingUrlScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                        .addComponent(configureButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startStopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(configureButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(streamingUrlScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void startStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopButtonActionPerformed
        StreamingModel model = controller.getStreamingModel();

        if (model == null) {
            return;
        }

        if (model.isServerRunning()) {
            controller.stopServer();
        } else {
            streamingUrlTextPane.setText("Starting...");
            controller.exposeWorkspaceAsStream();
        }
    }//GEN-LAST:event_startStopButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton configureButton;
    private javax.swing.JButton startStopButton;
    private javax.swing.JScrollPane streamingUrlScrollPane;
    private javax.swing.JTextPane streamingUrlTextPane;
    // End of variables declaration//GEN-END:variables

}