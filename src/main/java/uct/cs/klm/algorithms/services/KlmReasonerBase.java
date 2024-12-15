/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uct.cs.klm.algorithms.services;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;

/**
 *
 * @author ChipoHamayobe
 */
public abstract class KlmReasonerBase {

    protected final SatReasoner _reasoner;

    public KlmReasonerBase() {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        _reasoner = new SatReasoner();
    }
}
