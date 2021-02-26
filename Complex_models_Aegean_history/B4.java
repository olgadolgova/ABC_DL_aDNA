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
public class B4 extends B2 {

// Shared parameters that are inherited by any class that extends this one
// PARAMETERS OF EFFECTIVE POPULATION SIZES
    protected ParameterValue NeCHG_introgressed, NeCHGs;
// PARAMETERS OF TIMES
    protected ParameterValueScalable tSplitCHG_introgressed; 
    
    @Override
    protected void defineDemography() throws ParameterException {

// Basic populations to consider
String[] names = {"ModernGreek", "Logkas", "Manika", "Anatolian_N", "Yamnaya", "Steppe_introgressedLogkas", "CHG", "CHG_introgressed", "EHG", "EHG_introgressed"};
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
        demography.addSample ("Steppe_introgressedLogkas", 0); // Steppe introgressed in Logkas
        demography.addSample ("CHG_introgressed", 0); // CHG introgressed into Aegean BA
  
//         Modern human contamination
        demography.addContamination("ModernGreek", "Anatolian_N", new Value(0.01));
        demography.addContamination("ModernGreek", "Manika", new Value(0.0058));
        demography.addContamination("ModernGreek", "Logkas", new Value(0.0094));
   
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
       super.initializeModelParameters();

// EFFECTIVE POPULATION SIZES
        NeCHG_introgressed = new ParameterValue("NeCHG_introgressed", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeCHG_introgressed);
        demography.addEffectivePopulationSize("CHG_introgressed", NeCHG_introgressed);
                
// EVENTS
         
// Split CHG introgressed from CHG KK1    
        NeCHGs = new ParameterValue("NeCHGs", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeCHGs);
        tSplitCHG_introgressed = new ParameterValueScalable("tSplitCHG_introgressed", new DistributionUniformFromParameterValue(new Value(9811/29), tSplitAegeanN_CHG));
        tSplitCHG_introgressed.setScalable(tSplitAegeanN_CHG);
        parameters.add(tSplitCHG_introgressed);
        EventParameter emerge_CHGs = new EventParameter(tSplitCHG_introgressed, demography.getPosition("CHG_introgressed"), demography.getPosition("CHG"), new Value(1.0), NeCHG_introgressed, NeCHGs, new Value(0), 1);
        demography.add_event(emerge_CHGs);
// Introgression CHG into Aegean before the Aegean BA split
        ParameterValueScalable ptIntrogression_CHG_AegeanBA = new ParameterValueScalable("tIntrogression_CHG_AegeanBA", new DistributionUniformFromParameterValue(tSplitAegeanBA, tSplitAegeanBA_N));
        ptIntrogression_CHG_AegeanBA.setScalable(tSplitAegeanBA);
        parameters.add(ptIntrogression_CHG_AegeanBA);
        ParameterValue introgression_CHG_AegeanBA = new ParameterValue("introgression_CHG_AegeanBA", new DistributionUniformFromValue(new Value(0.001), new Value(0.8)));
        parameters.add(introgression_CHG_AegeanBA);
        EventParameter eIntrogression_CHG_AegeanBA = new EventParameter(ptIntrogression_CHG_AegeanBA, demography.getPosition("Manika"), demography.getPosition("CHG_introgressed"), introgression_CHG_AegeanBA, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression_CHG_AegeanBA);
    }
}


