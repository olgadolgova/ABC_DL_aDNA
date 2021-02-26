/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Complex_models_Aegean_history;

import fastsimcoal2.ParameterException;
import fastsimcoal2.event.demography.Demography;
import fastsimcoal2.event.demography.EventParameter;
import fastsimcoal2.parameter.ParameterValue;
import fastsimcoal2.parameter.ParameterValueScalable;
import fastsimcoal2.parameter.Value;
import fastsimcoal2.parameter.distribution.DistributionUniformFromParameterValue;
import fastsimcoal2.parameter.distribution.DistributionUniformFromValue;

/**
 * 
 *
 * @author Olga Dolgova
 */
public class B2 extends B1 {

// Shared parameters that are inherited by any class that extends this one
// PARAMETERS OF EFFECTIVE POPULATION SIZES
    protected ParameterValue NeSteppe_introgressedLogkas, NeSteppes;
// PARAMETERS OF TIMES
    protected ParameterValueScalable tSplitSteppe_introgressed;
    
    @Override
    protected void defineDemography() throws ParameterException {

// Basic populations to consider
String[] names = {"ModernGreek", "Logkas", "Manika", "Anatolian_N", "Yamnaya", "Steppe_introgressedLogkas", "CHG", "EHG", "EHG_introgressed"};
// Add the populations
        demography = new Demography(names);
// Modern populations        
        demography.addSample("ModernGreek", 2);
// Ancient populations   
        this.percentage_ACTG_in_genome[0] = 0.3;
        this.percentage_ACTG_in_genome[1] = 0.2;
        this.percentage_ACTG_in_genome[2] = 0.3;
        this.percentage_ACTG_in_genome[3] = 0.2;
        
        demography.addSampleWithTime("Anatolian_N", 8244 / 29, 2); // Anatolian Bar8 genome
        demography.setPopulationIIsAncient("Anatolian_N", new Value(0.0088), new Value(0.0088), new Value(5));
        demography.addSampleWithTime("Logkas", 3976 / 29, 2); // Log04 genome
        demography.setPopulationIIsAncient("Logkas", new Value(0.0105), new Value(0.0105), new Value(4));
        demography.addSampleWithTime("Manika", 4847 / 29, 2);   // Mik15 genome
        demography.setPopulationIIsAncient("Manika", new Value(0.0148), new Value(0.0148), new Value(2));
        demography.addSampleWithTime("Yamnaya", 4972 / 29, 2); // Yamnaya_Karagash_EMBA genome
        demography.setPopulationIIsAncient("Yamnaya", new Value(0.009), new Value(0.009), new Value(18));
        demography.addSampleWithTime("CHG", 9782 / 29, 2); // CHG KK1 genome
        demography.setPopulationIIsAncient("CHG", new Value(0.0085), new Value(0.0085), new Value(17));
        demography.addSampleWithTime("EHG", 11328 / 29, 2); // Sidelkino genome
        demography.setPopulationIIsAncient("EHG", new Value(0.014), new Value(0.014), new Value(2));
        demography.addSample ("EHG_introgressed", 0); // EHG introgressed into Steppe
        demography.addSample ("Steppe_introgressedLogkas", 0); // Steppe introgressed to Aegean MBA
     
        
////         //         Modern human contamination
        demography.addContamination("ModernGreek", "Anatolian_N", new Value(0.01));
        demography.addContamination("ModernGreek", "Manika", new Value(0.0058));
        demography.addContamination("ModernGreek", "Logkas", new Value(0.0094));
       
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
       super.initializeModelParameters();

// EFFECTIVE POPULATION SIZES
        NeSteppe_introgressedLogkas = new ParameterValue("NeSteppe_introgressedLogkas", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeSteppe_introgressedLogkas);
        demography.addEffectivePopulationSize("Steppe_introgressedLogkas", NeSteppe_introgressedLogkas);
        
// Split Yamnayas introgressed Logkas from Yamnaya Karagash     
        NeSteppes = new ParameterValue("NeSteppes", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeSteppes);
        tSplitSteppe_introgressed = new ParameterValueScalable("tSplitSteppe_introgressedLogkas", new DistributionUniformFromParameterValue(new Value(5001/29), ptIntrogression_EHG_Steppe));
        tSplitSteppe_introgressed.setScalable(ptIntrogression_EHG_Steppe);
        parameters.add(tSplitSteppe_introgressed);
        EventParameter emerge_Steppes = new EventParameter(tSplitSteppe_introgressed, demography.getPosition("Steppe_introgressedLogkas"), demography.getPosition("Yamnaya"), new Value(1.0), NeSteppe_introgressedLogkas, NeSteppes, new Value(0), 1);
        demography.add_event(emerge_Steppes);
        
// Introgression of Steppe in Logkas     
        ParameterValueScalable ptIntrogression_Steppe_AegeanMBA = new ParameterValueScalable("tIntrogression_Steppe_AegeanMBA", new DistributionUniformFromParameterValue(new Value (4005 / 29), tSplitAegeanBA));
        ptIntrogression_Steppe_AegeanMBA.setScalable(tSplitAegeanBA);
        parameters.add(ptIntrogression_Steppe_AegeanMBA);
        ParameterValue introgression_Steppe_AegeanMBA = new ParameterValue("introgression_Steppe_AegeanMBA", new DistributionUniformFromValue(new Value(0.001), new Value(0.8)));
        parameters.add(introgression_Steppe_AegeanMBA);
        EventParameter eIntrogression_Steppe_AegeanMBA = new EventParameter(ptIntrogression_Steppe_AegeanMBA, demography.getPosition("Logkas"), demography.getPosition("Steppe_introgressedLogkas"), introgression_Steppe_AegeanMBA, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression_Steppe_AegeanMBA);
        
    }
}


