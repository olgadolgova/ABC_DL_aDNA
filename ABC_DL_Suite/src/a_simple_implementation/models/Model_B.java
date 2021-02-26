/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_simple_implementation.models;

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
 * Model_B. Identical to Model A but including an introgression event in forward population 3 to 2
 * @author Olga Dolgova
 */
public class Model_B extends FastSimcoalModel {

    @Override
    protected void defineDemography() throws ParameterException {
        // Names of the populations to be used in fastSimcoal2
        String[] names = {"Pop1", "Pop2", "Pop3", "Pop4"};
        
        demography = new Demography(names);

        // How many chromosomes are sampled from each population? In principle, only two chromosomes (one individual). This line of code is for the first 2 modern populations only.
        for (int pop = 0; pop < 2; pop++) {
            demography.addSample(names[pop], 2);
        }
//Ancient populations
        this.percentage_ACTG_in_genome[0] = 0.3;
        this.percentage_ACTG_in_genome[1] = 0.2;
        this.percentage_ACTG_in_genome[2] = 0.3;
        this.percentage_ACTG_in_genome[3] = 0.2;

        demography.addSampleWithTime("Pop3", 1800, 2); //Archaic population 1 is sampled 1800 generations ago

        demography.setPopulationIIsAncient("Pop3", new Value(0.0088), new Value(0.0088), new Value(5));
//The proportions of C->T and G->A substitutions and the mean depth of coverage of archaic population 1.

        demography.addSampleWithTime("Pop4", 1414, 2); //Archaic population 2 is sampled 1414 generations ago 

        demography.setPopulationIIsAncient("Pop4", new Value(0.0105), new Value(0.0105), new Value(4));
//The proportions of C->T and G->A substitutions and the mean depth of coverage of archaic population 2.

//Modern human contamination
        demography.addContamination("Pop2", "Pop3", new Value(0.01)); 
        demography.addContamination("Pop2", "Pop4", new Value(0.0058));
//First population is a source of contamination, second is a sink, followed by the proportion of sequencing errors due to contamination

    }

    @Override
    protected void initializeModelParameters() throws ParameterException {
// PARAMETERS
// EFFECTIVE POPULATION SIZES
    ParameterValue Ne1, Ne2, Ne3, Ne4, Ne1_2, Ne3_4, Ne1_2_3_4;
// MIGRATION RATES
// Our model assumes no migration events. Check class Model_Real to see how to include migration events
// EFFECTIVE POPULATION SIZES
// The effective population size of pop 1 can range A PRIORI uniformly between 1000 chromosomes and 5000 chromosomes
        Ne1 = new ParameterValue("Ne1", new DistributionUniformFromValue(new Value(1000), new Value(5000)));
        parameters.add(Ne1);
        demography.addEffectivePopulationSize("Pop1", Ne1);
// The effective population size of pop 2 can range A PRIORI between 500 and 1000
        Ne2 = new ParameterValue("Ne2", new DistributionUniformFromValue(new Value(500), new Value(1000)));
        parameters.add(Ne2);
        demography.addEffectivePopulationSize("Pop2", Ne2);
// The effective population size of pop 3 can range between 10000 and 20000
        Ne3 = new ParameterValue("Ne3", new DistributionUniformFromValue(new Value(10000), new Value(20000)));
        parameters.add(Ne3);
        demography.addEffectivePopulationSize("Pop3", Ne3);                        
// The effective population size of pop 4 can range between 5000 and 10000
        Ne4 = new ParameterValue("Ne4", new DistributionUniformFromValue(new Value(5000), new Value(10000)));
        parameters.add(Ne4);
        demography.addEffectivePopulationSize("Pop4", Ne4);   


// TIME EVENTS
    ParameterValue t1_2, t3_4, tintrogression3to2;
// TIME EVENTS THAT DEPEND ON ANOTHER EVENT.
// In this case, the time of split of the ancestors of (pop1,2) and (pop3,4) cannot be younger than the time of split of pop3,4    
    ParameterValueScalable t1_2_3_4;        
// EVENTS
// Event of introgression. Population 2 sends backward in time migrants to Population 3
// Event Archaic introgression of population 3 in 2 between 200 and 400 generations ago
        tintrogression3to2 = new ParameterValue("tIntrogressionPop3_to_Pop2", new DistributionUniformFromValue(new Value(200), new Value(400)));
        parameters.add(tintrogression3to2);
// Introgression ranges between 1% and 20%    
        ParameterValue introgression = new ParameterValue("IntrogressionPop3_to_Pop2", new DistributionUniformFromValue(new Value(0.01), new Value(0.2)));
        parameters.add(introgression);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter eIntrogression3_to_2 = new EventParameter(tintrogression3to2, demography.getPosition("Pop2"), demography.getPosition("Pop3"), introgression, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression3_to_2);

// Event Archaic introgression of population 3 in 2 between 200 and 400 generations ago
        ParameterValue tintrogression4to2 = new ParameterValue("tIntrogressionPop4_to_Pop2", new DistributionUniformFromValue(new Value(200), new Value(400)));
        parameters.add(tintrogression4to2);
// Introgression ranges between 1% and 20%    
        ParameterValue introgression4to2 = new ParameterValue("IntrogressionPop4_to_Pop2", new DistributionUniformFromValue(new Value(0.01), new Value(0.2)));
        parameters.add(introgression4to2);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter eIntrogression4_to_2 = new EventParameter(tintrogression3to2, demography.getPosition("Pop2"), demography.getPosition("Pop4"), introgression4to2, new Value(1.0), new Value(1.0), new Value(0), 1);
        demography.add_event(eIntrogression4_to_2);        
        
// Event Split pop1 from pop2. It ranges between 500 generations and 1500 generations
        t1_2 = new ParameterValue("tSplitPop1_Pop2", new DistributionUniformFromValue(new Value(500), new Value(1500)));
        parameters.add(t1_2);
// The effective population size of this merged population can be between 100 and 200        
        Ne1_2 = new ParameterValue("Ne1_2", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne1_2);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit1_2 = new EventParameter(t1_2, demography.getPosition("Pop1"), demography.getPosition("Pop2"), new Value(1.0), Ne2, Ne1_2, new Value(0), 1);
        demography.add_event(etSplit1_2);

// Event Split pop3 from pop4.        
        t3_4 = new ParameterValue("tSplitPop3_Pop4", new DistributionUniformFromValue(new Value(2000), new Value(4000)));
        parameters.add(t3_4);
// The effective population size of this merged population can be between 100 and 200        
        Ne3_4 = new ParameterValue("Ne3_4", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne3_4);
// In fastSimcoal2, a split event forward is defined as an event backward where one of the two populations merges all its migrants with the other population.        
        EventParameter etSplit3_4 = new EventParameter(t3_4, demography.getPosition("Pop3"), demography.getPosition("Pop4"), new Value(1.0), Ne4, Ne3_4, new Value(0), 1);
        demography.add_event(etSplit3_4);
        
// Event split pop1_2 from pop3_4        
        t1_2_3_4 = new ParameterValueScalable("tSplitPop1_Pop2_Pop3_Pop4", new DistributionUniformFromParameterValue(t3_4, new Value(6000)));
        t1_2_3_4.setScalable(t3_4);
        parameters.add(t1_2_3_4);
        Ne1_2_3_4 = new ParameterValue("Ne1_2_3_4", new DistributionUniformFromValue(new Value(100), new Value(200)));
        parameters.add(Ne1_2_3_4);
        EventParameter etSplit1_2_3_4 = new EventParameter(t1_2_3_4, demography.getPosition("Pop2"), demography.getPosition("Pop4"), new Value(1.0), Ne3_4, Ne1_2_3_4, new Value(0), 1);
        demography.add_event(etSplit1_2_3_4);
    }
}
