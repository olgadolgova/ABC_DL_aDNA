rm(list=ls());
if("abc" %in% rownames(installed.packages()) == FALSE)
{
 stop("abc package is required"); 
}
library("abc"); # load the abc package
abc_folder <-"C:/Users/odolgova/OneDrive - CRG - Centre de Regulacio Genomica/Olga/Bronze Age Greece/Final_corrections/ABC-DL_OL_github/ABC_DL-master/ABC_DL_Example_Project/test_model"; # CHANGE TO THE FOLDER WHERE YOU RUN THE SIMULATIONS
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
data.t <- read.table(file=paste("model_predictions.txt"),header=F);
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


