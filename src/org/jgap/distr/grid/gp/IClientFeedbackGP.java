/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap.distr.grid.gp;

import java.io.*;

/**
 * Interface for the feedback a client receives as a listener.
 *
 * @author Klaus Meffert
 * @since 3.2
 */
public interface IClientFeedbackGP
    extends Serializable {
  /** String containing the CVS revision. Read out via reflection!*/
  final static String CVS_REVISION = "$Revision: 1.2 $";

  void setProgressMinimum(int min);

  void setProgressMaximum(int max);

  void setProgressValue(int val);

  void beginWork();

  void sendingFragmentRequest(JGAPRequestGP req);

  void receivedFragmentResult(JGAPRequestGP req, JGAPResultGP res, int idx);

  void endWork();

  void completeFrame(int idx);

  void error(String msg, Exception ex);

  void info(String msg);
}
