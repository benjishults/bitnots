/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bitnots.gui;

import bitnots.theories.TheoremApplication;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author bshults
 */
class SelectTheoremApplicationView extends JPanel {

  private Component[] replaces;
  private List<TheoremApplication> taList;

  /**
   * This replaces this in its parent with replaces.
   */
  private void replace() {
    TheoryView.AxiomPanel parent = (TheoryView.AxiomPanel) this.getParent();
    parent.removeAll();
    parent.setAxiomLayout();
    for (Component c : this.replaces) {
      parent.add(c);
    }
  }

  SelectTheoremApplicationView(List<TheoremApplication> tas,
                               Component[] replaces) {
    this.replaces = replaces;
    this.taList = tas;
    // XXX lay out TheoremApplications and let user select on to apply.
    this.setLayout(new GridLayout(this.taList.size() + 1, 1, 10, 5));
    // XXX display the selected sequent at the top
    if (tas.isEmpty()) {
      // XXX report that nothing applies
    } else {
      this.add(new JTree(new DefaultTreeModel(new KBSequentTree(tas.get(0).
          getSequent()))));
      for (TheoremApplication ta : this.taList) {
        this.add(new TheoremApplicationView(ta));
        // XXX on selection and application of a TheoremApplication, replace
        // this in its parent with replaces.
      }
    }
    // include a "cancel" button that will replace this in its parent
    // with replaces.
    JButton cancel = new JButton("Cancel");
    this.add(cancel);
    cancel.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        SelectTheoremApplicationView.this.replace();
      }
    });
  }
}
