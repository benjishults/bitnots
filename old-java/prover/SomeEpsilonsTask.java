package bitnots.prover;

import bitnots.gui.Bitnots;
import bitnots.tableaux.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Benjamin Shults
 * @version
 */
public class SomeEpsilonsTask
    extends AbstractTableauTask {

  public SomeEpsilonsTask(Tableau tab) {
    super(tab);
  }

  public void run() {
    // expand tree as much as possible with alphas, deltas,
    // gammas, and betas - when that is done, apply an epsilon;
    // then apply all the other formulas again
    if (Prover.multiProve(this.tableau)) {
      if (Prover.unifyEquality(this.tableau) != null)
        // if the above succeeded, tell the UI.
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(Bitnots.FRAME,
                                          "Done: " + Bitnots.FRAME.getTableau().
                getCloser());
          }
        });
      return;
    }
    if (this.tableau.getEpsilonToolkit().applySomeEpsilons()) {
      if (Prover.unifyEquality(this.tableau) != null)
        // if the above succeeded, tell the UI.
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(Bitnots.FRAME,
                                          "Done: " + Bitnots.FRAME.getTableau().
                getCloser());
          }
        });
      return;
    }
  }
}

