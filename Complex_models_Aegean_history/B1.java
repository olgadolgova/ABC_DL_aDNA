/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Complex_models_Aegean_history;

import models.*;
import fastsimcoal2.FastSimcoalModel;
import fastsimcoal2.ParameterException;
import fastsimcoal2.event.demography.Demography;
import fastsimcoal2.event.demography.EventParameter;
import fastsimcoal2.parameter.ParameterValue;
import fastsimcoal2.parameter.ParameterValueScalable;
import fastsimcoal2.parameter.Value;
import fastsimcoal2.parameter.distribution.DistributionUniformFromParameterValue;
import fastsimcoal2.parameter.distribution.DistributionUniformFromValue;

/**
 * Define the basic topology model. (((Aegean_N,((Aegean MBA, ModernGreek), Aegean EBA)), CHG),(EHG, Steppe)). 
 * @author Olga Dolgova
 */
public class B1 extends FastSimcoalModel {

// Shared parameters that are inherited by any class that extends this one
// PARAMETERS OF EFFECTIVE POPULATION SIZES
    protected ParameterValue NeModernGreek, NeAegeanMBA, NeAegeanMBA_ModernGreek, NeAegeanEBA, NeAegeanBA, NeAegeanN, NeAegeans, NeSteppe, NeCHG, NeSteppe_CHG, NeEHG, NeEHG_introgressed, NeEHGs, NeAegeanN_CHG, NeAegeanCHG_EHG;
// PARAMETERS OF TIMES
    protected ParameterValue tSplitAegeanMBA_ModernGreek, tSplitAegeanBA, tSplitAegeanBA_N, tSplitSteppe_CHG, tSplitAegeanN_CHG;
    protected ParameterValueScalable tSplitEHG_introgressed, tSplitAegeanCHG_EHG, ptIntrogression_EHG_Steppe;

    @Override
    protected void defineDemography() throws ParameterException {

// Basic populations to consider
String[] names = {"ModernGreek", "Logkas", "Manika", "Anatolian_N", "Yamnaya", "CHG", "EHG", "EHG_introgressed"};
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
        
//        Modern human contamination
        demography.addContamination("ModernGreek", "Anatolian_N", new Value(0.01));
        demography.addContamination("ModernGreek", "Manika", new Value(0.0058));
        demography.addContamination("ModernGreek", "Logkas", new Value(0.0094));
     
                    
    }

    @Override
    protected void initializeModelParameters() throws ParameterException {

       
// EFFECTIVE POPULATION SIZES
        NeAegeanN = new ParameterValue("NeAegeanN", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanN);
        demography.addEffectivePopulationSize("Anatolian_N", NeAegeanN);

        NeSteppe = new ParameterValue("NeSteppe_EMBA", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeSteppe);
        demography.addEffectivePopulationSize("Yamnaya", NeSteppe);
        
        NeModernGreek = new ParameterValue("NeModernGreek", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeModernGreek);
        demography.addEffectivePopulationSize("ModernGreek", NeModernGreek);
                                                
        NeAegeanMBA = new ParameterValue("NeAegeanMBA", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanMBA);
        demography.addEffectivePopulationSize("Logkas", NeAegeanMBA);
        
        NeAegeanEBA = new ParameterValue("NeAegeanEBA", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanEBA);
        demography.addEffectivePopulationSize("Manika", NeAegeanEBA);
        
        NeCHG = new ParameterValue("NeCHG", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeCHG);
        demography.addEffectivePopulationSize("CHG", NeCHG);
        
        NeEHG = new ParameterValue("NeEHG", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeEHG);
        demography.addEffectivePopulationSize("EHG", NeEHG);
        
        NeEHG_introgressed = new ParameterValue("NeEHG_introgressed", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeEHG_introgressed);
        demography.addEffectivePopulationSize("EHG_introgressed", NeEHG_introgressed);
                    
      
// EVENTS
// Split Modern Greek from Aegean MBA
        tSplitAegeanMBA_ModernGreek = new ParameterValue("tSplitAegeanMBA_ModernGreek", new DistributionUniformFromValue(new Value(4000/29), new Value(5000/29)));
        parameters.add(tSplitAegeanMBA_ModernGreek);
        NeAegeanMBA_ModernGreek = new ParameterValue("NeLogkas_ModernGreek", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanMBA_ModernGreek);
        EventParameter emerge_ModernGreek_AegeanMBA = new EventParameter(tSplitAegeanMBA_ModernGreek, demography.getPosition("ModernGreek"), demography.getPosition("Logkas"), new Value(1.0), NeModernGreek, NeAegeanMBA_ModernGreek, new Value(0), 1);
        demography.add(emerge_ModernGreek_AegeanMBA);
// Split Aegean MBA from Aegean EBA
        tSplitAegeanBA = new ParameterValue("tSplitAegeanBA", new DistributionUniformFromValue(new Value(5029/29), new Value(8030/29)));
        parameters.add(tSplitAegeanBA);
        NeAegeanBA = new ParameterValue("NeAegeanBA", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanBA);
        EventParameter emerge_AegeanBA = new EventParameter(tSplitAegeanBA, demography.getPosition("Logkas"), demography.getPosition("Manika"), new Value(1.0), NeAegeanEBA, NeAegeanBA, new Value(0), 1);
        demography.add(emerge_AegeanBA);
// Split Neolithic Anatolian farmers from Aegean BA
        tSplitAegeanBA_N = new ParameterValue("tSplitAegeans", new DistributionUniformFromValue(new Value (8273/29), new Value(13000/29)));
        parameters.add(tSplitAegeanBA_N);
        NeAegeans = new ParameterValue("NeAegeans", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeans);
        EventParameter emerge_Aegean = new EventParameter(tSplitAegeanBA_N, demography.getPosition("Manika"), demography.getPosition("Anatolian_N"), new Value(1.0), NeAegeanBA, NeAegeans, new Value(0), 1);
        demography.add(emerge_Aegean);      
// Split Steppe and CHG
        tSplitSteppe_CHG = new ParameterValue("tSplitSteppe_CHG", new DistributionUniformFromValue(new Value(9811/29), new Value(15000/29)));
        parameters.add(tSplitSteppe_CHG);
        NeSteppe_CHG = new ParameterValue("NeSteppe_CHG", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeSteppe_CHG);
        EventParameter emerge_Steppe_CHG = new EventParameter(tSplitSteppe_CHG, demography.getPosition("Yamnaya"), demography.getPosition("CHG"), new Value(1.0), NeSteppe, NeCHG, new Value(0), 1);
        demography.add(emerge_Steppe_CHG);
// Split EHG introgressed
        tSplitEHG_introgressed = new ParameterValueScalable("tSplitEHG_introgressed", new DistributionUniformFromParameterValue(new Value(11357/29), tSplitAegeanCHG_EHG)); 
        tSplitEHG_introgressed.setScalable(tSplitAegeanCHG_EHG);
        parameters.add(tSplitEHG_introgressed);
        NeEHGs = new ParameterValue("NeEHGs", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeEHGs);
        EventParameter emerge_EHGs = new EventParameter(tSplitEHG_introgressed, demography.getPosition("EHG_introgressed"), demography.getPosition("EHG"), new Value(1.0), NeEHG_introgressed, NeEHGs, new Value(0), 1);
        demography.add(emerge_EHGs);
// Split Yamnaya_CHG and EHG
        tSplitAegeanN_CHG = new ParameterValue("tSplitAegeanN_CHG", new DistributionUniformFromValue(new Value(13029/29), new Value(40000/29))); 
        parameters.add(tSplitAegeanN_CHG);
        NeAegeanN_CHG = new ParameterValue("NeAegeanN_CHG", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanN_CHG);
        EventParameter emerge_AegeanN_CHG = new EventParameter(tSplitAegeanN_CHG, demography.getPosition("Anatolian_N"), demography.getPosition("CHG"), new Value(1.0), NeAegeans, NeAegeanN_CHG, new Value(0), 1);
        demography.add(emerge_AegeanN_CHG);
// Split Aegean_CHG from Eurpean Hunter Gatherers
        tSplitAegeanCHG_EHG = new ParameterValueScalable("tSplitAegeansCHG_EHG", new DistributionUniformFromParameterValue(tSplitAegeanN_CHG, new Value(50000/29))); 
        tSplitAegeanCHG_EHG.setScalable(tSplitAegeanN_CHG);
        parameters.add(tSplitAegeanCHG_EHG);
        NeAegeanCHG_EHG = new ParameterValue("NeAegeansCHG_EHG", new DistributionUniformFromValue(new Value(1000), new Value(20000)));
        parameters.add(NeAegeanCHG_EHG);
        EventParameter emerge_AnatolianCHG_EHG = new EventParameter(tSplitAegeanCHG_EHG, demography.getPosition("CHG"), demography.getPosition("EHG"), new Value(1.0), NeAegeanN_CHG, NeAegeanCHG_EHG, new Value(0), 1);
        demography.add(emerge_AnatolianCHG_EHG);
// Introgression EHG into Yamnaya     
        ptIntrogression_EHG_Steppe = new ParameterValueScalable("tIntrogression_EHG_Steppe", new DistributionUniformFromParameterValue(new Value(5000/29), tSplitSteppe_CHG));
        ptIntrogression_EHG_Steppe.setScalable(tSplitSteppe_CHG);
        parameters.add(ptIntrogression_EHG_Steppe);
        ParameterValue introgression_EHG_Yamnaya = new ParameterValue("introgression_EHG_Steppe", new DistributionUniformFromValue(new Value(0.001), new Value(0.8)));
        parameters.add(introgression_EHG_Yamnaya);
        EventParameter eIntrogression_EHG_Yamnaya = new EventParameter(ptIntrogression_EHG_Steppe, demography.getPosition("Yamnaya"), demography.getPosition("EHG_introgressed"), introgression_EHG_Yamnaya, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression_EHG_Yamnaya);
        
    }
}


