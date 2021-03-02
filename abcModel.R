rm(list=ls());
if("abc" %in% rownames(installed.packages()) == FALSE)
{
 stop("abc package is required"); 
}
library("abc"); # load the abc package
<<<<<<< Updated upstream:abcModel.R
abc_folder <- “path_to_abc_dl”; # CHANGE TO THE FOLDER WHERE YOU RUN THE SIMULATIONS
=======
abc_folder <-"Path/to/your/directory"; # CHANGE TO THE FOLDER WHERE YOU RUN THE SIMULATIONS
>>>>>>> Stashed changes:ABC_DL_Example_Project/abcModel.R
# now, for each of the neural network replicates, compute the mean distance. Return the sum
compute.mean.nn <- function(simulated.ss, nen, n.models)
{
  simulated.ss.by.nn <- matrix(nrow=nrow(simulated.ss),ncol=n.models, rep(0,nrow(simulated.ss)*n.models));
  
  for(nn in 1:nen)
  {
    sequence <- seq(from=(1+n.models*(nn-1)),to = (n.models+n.models*(nn-1)),by=1);
    simulated.ss.by.nn <- simulated.ss.by.nn + simulated.ss[,sequence];
  }
  return(simulated.ss.by.nn/nen);
}
# data with the model predictions from the DL
<<<<<<< Updated upstream:abcModel.R
data.t <- read.table(file=paste(abc_folder,”model_predictions.txt”,sep=”\\”),header=F);
=======
data.t <- read.table(file=paste("model_predictions.txt"),header=F);
>>>>>>> Stashed changes:ABC_DL_Example_Project/abcModel.R
# first row is the observed data
observed.ss <- data.t[which(data.t[,1]=="observed"),2:ncol(data.t)]
# summary statistics of the simulated data. Remember. If we have K models, each K columns correspond to a NN prediction.
simulated.ss <- as.matrix(data.t[-which(data.t[,1]=="observed"),2:ncol(data.t)]);
# names of the models
models.by.row <- data.t[-which(data.t[,1]=="observed"),1];
# levels of the models
model.names <- levels(as.factor(as.character(models.by.row)));
# set each model as an integer
models <- rep(-1,nrow(simulated.ss));
for(m in 1:length(model.names)) {
  models[models.by.row==model.names[m]] <- m;
}


# compute the mean predicted value over all the NN for each possible model. First parameter is the matrix of simulations and predictions. Second parameter is the number of neural networks that we have run. The third parameter is the number of models.
mean.ss <- compute.mean.nn(simulated.ss, ncol(simulated.ss)/ length(model.names), length(model.names));
# do the same for the observed data.
mean.observed.ss <- compute.mean.nn(observed.ss, ncol(simulated.ss)/ length(model.names), length(model.names));
# use postpr to generate the posterior distribution of the data. Check the function in abc package.
res <- postpr(mean.observed.ss, models, mean.ss, tol=1000/nrow(simulated.ss),method="mnlogistic");
summary(res)
model.names

<<<<<<< Updated upstream:abcModel.R
#To calculate the confusion matrix out of 100 simulations per model to see to which extend the simulations generated under particular model (rows) are properly assigned to this model (columns)
=======

>>>>>>> Stashed changes:ABC_DL_Example_Project/abcModel.R
confussion.matrix <- matrix(nrow=length(model.names), ncol=length(model.names),rep(0,length(model.names)*length(model.names)));
rownames(confussion.matrix) <- as.character(model.names);
colnames(confussion.matrix) <- as.character(model.names);


for(m in 1:length(model.names))
{
  ids <- which(models==m);
  print(m);
  for(rep in 1:100) 
  {
    use.id <- sample(ids,1);
    mean.ss.observed.nn <- mean.ss[use.id,];
    res <- postpr(mean.ss.observed.nn, models[-use.id],mean.ss[-use.id,],tol=1000/nrow(mean.ss),method="mnlogistic");
    ex <- summary(res,print = FALSE)$mnlogistic$Prob;
    if(is.null(ex))
    {
      ex <- summary(res, print = FALSE)$Prob;
    }
    model.max <- which(ex==max(ex));
    confussion.matrix[m,model.max[1]] <- confussion.matrix[m,model.max[1]] + 1;
  }
}
confussion.matrix

<<<<<<< Updated upstream:abcModel.R
=======

>>>>>>> Stashed changes:ABC_DL_Example_Project/abcModel.R
