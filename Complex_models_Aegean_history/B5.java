/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Complex_models_Aegean_history;

import models.*;
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
public class B5 extends B4 {

// Shared parameters that are inherited by any class that extends this one
// PARAMETERS OF EFFECTIVE POPULATION SIZES
    protected ParameterValue NeEHG_introgressedAegean, NeAllEHGs;
// PARAMETERS OF TIMES
    protected ParameterValueScalable tSplitEHG_introgressedAegean; 
    
    @Override
    protected void defineDemography() throws ParameterException {

// Basic populations to consider
String[] names = {"ModernGreek", "Logkas", "Manika", "Anatolian_N", "Yamnaya", "Steppe_introgressedLogkas", "CHG", "CHG_introgressed", "EHG", "EHG_introgressed", "EHG_introgressedAegean"};
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
        demography.addSample ("EHG_introgressed", 0); // EHG introgressed into Yamnaya
        demography.addSample ("CHG_introgressed", 0); // CHG introgressed into Aegean BA
        demography.addSample ("Steppe_introgressedLogkas", 0); // Steppe introgressed in Logkas
        demography.addSample ("EHG_introgressedAegean", 0); // EHG introgressed in Aegean BA
        
        
        // Modern human contamination
        demography.addContamination("ModernGreek", "Anatolian_N", new Value(0.01));
        demography.addContamination("ModernGreek", "Manika", new Value(0.0058));
        demography.addContamination("ModernGreek", "Logkas", new Value(0.0094));
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
       super.initializeModelParameters();

// EFFECTIVE POPULATION SIZES
        NeEHG_introgressedAegean = new ParameterValue("NeEHG_introgressedBAAegeans", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeEHG_introgressedAegean);
        demography.addEffectivePopulationSize("EHG_introgressedAegean", NeEHG_introgressedAegean);
        
            
// EVENTS
          
        
// Split EHGs introgressed from EHG Sidelkino
        NeAllEHGs = new ParameterValue("NeAllEHGs", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAllEHGs);
        tSplitEHG_introgressedAegean = new ParameterValueScalable("tSplitEHG_introgressedBAAegeans", new DistributionUniformFromParameterValue(new Value(11328 / 29), tSplitAegeanCHG_EHG));
        tSplitEHG_introgressedAegean.setScalable(tSplitAegeanCHG_EHG);
        parameters.add(tSplitEHG_introgressedAegean);
        EventParameter emerge_EHGs = new EventParameter(tSplitEHG_introgressedAegean, demography.getPosition("EHG_introgressedAegean"), demography.getPosition("EHG"), new Value(1.0), NeEHG_introgressedAegean, NeAllEHGs, new Value(0), 1);
        demography.add_event(emerge_EHGs);

// Introgression EHG into Aegean     
        ParameterValueScalable ptIntrogression_EHG_AegeanBA = new ParameterValueScalable("tIntrogression_EHG_AegeanBA", new DistributionUniformFromParameterValue(tSplitAegeanBA, tSplitAegeanBA_N));
        ptIntrogression_EHG_AegeanBA.setScalable(tSplitAegeanBA);
        parameters.add(ptIntrogression_EHG_AegeanBA);
        ParameterValue introgression_EHG_AegeanBA = new ParameterValue("introgression_EHG_AegeanBA", new DistributionUniformFromValue(new Value(0.001), new Value(0.8)));
        parameters.add(introgression_EHG_AegeanBA);
        EventParameter eIntrogression_EHG_AegeanBA = new EventParameter(ptIntrogression_EHG_AegeanBA, demography.getPosition("Manika"), demography.getPosition("EHG_introgressedAegean"), introgression_EHG_AegeanBA, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression_EHG_AegeanBA);
    }
}


