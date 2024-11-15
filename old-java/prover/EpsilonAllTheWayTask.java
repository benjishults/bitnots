package bitnots.prover;

import bitnots.gui.Bitnots;
import bitnots.tableaux.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * OneEpsilonTask.java
 *
 * Created: Wed Jun 09 15:43:05 2004
 * @author Daniel W. Farmer
 * @version 1.0
 */

public class EpsilonAllTheWayTask extends AbstractTableauTask {

  public EpsilonAllTheWayTask(Tableau tab) {
    super(tab);
  }

  @SuppressWarnings("empty-statement")
  public void run() {
    // expand tree as much as possible with alphas, deltas,
    // gammas, and betas - when that is done, apply an epsilon;
    // then start over

    while (true) {
      while (Prover.multiProve(this.tableau));

      if (Prover.unifyEquality(this.tableau) != null) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(Bitnots.FRAME,
                                          "Done: " + Bitnots.FRAME.getTableau().
                getCloser());
          }
        });
        return;
      }
      if (this.tableau.getEpsilonToolkit().applySomeEpsilons())
        continue;
      else
        break;
    }
  }
}
