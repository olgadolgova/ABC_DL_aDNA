/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abc_dl_suite.handleOutputFiles;

import abc_deeplearning.model.Load_Model_Data;
import abc_dl_suite.project_information.ProjectInformation;
import data_management.DataManagement;
import data_management.RetrieveDataByRow;
import fastsimcoal2.FastSimcoalModel;
import java.io.File;
import util.Parameters_SummaryStatistics;
import write.WriteFile;

/**
 *
 * @author Oscar Lao
 */
public class GenerateFoldedPairwiseSFS {

    /**
     * Generate a FolderPairwise file
     * @param pi the project information
     * @param model the model
     * @param training shall we do it with the training (true) or the replication (false) ?
     * @throws Exception if something goes wrong.
     */
    public static void generateFoldedPairwiseSFS(ProjectInformation pi, int model, boolean training) throws Exception {
        int model_to_run = model % pi.getLm().getBmodel().length;

        String coletilla = (training) ? "_training" : "_replication";        

        FastSimcoalModel bmodel = pi.getLm().getBmodel()[model_to_run];

        String[] individualNames = pi.getIndividuals_Training();
        bmodel.defineModel();
        WriteFile wf = new WriteFile(pi.getWorking_folder() + "output_folded" + bmodel.modelName() + coletilla + ".txt");
        wf.println(bmodel.getLabelOfParameters());

        RetrieveDataByRow rdb = new RetrieveDataByRow(new File(pi.getWorking_folder() + "output_" + bmodel.modelName() + coletilla + ".txt"));

        Parameters_SummaryStatistics ps = rdb.next();

        while (ps != null) {
            double[] sfs = DataManagement.getSFSTriplets(individualNames.length, ps.getSummaryStatistics());
            double[] param = ps.getParameters();
            wf.print(param[0]);
            for (int r = 1; r < param.length; r++) {
                wf.print(" " + param[r]);
            }

            for (int p = 0; p < sfs.length; p++) {
                wf.print(" " + sfs[p]);
            }
            wf.println("");
            ps = rdb.next();
        }    
        wf.close();
    }
}
