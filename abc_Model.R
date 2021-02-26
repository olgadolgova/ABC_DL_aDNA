#####################

# ABC observed data #

#####################

install.packages("abc")
##########################################

#  MODELS                                #

##########################################

rm(list=ls());
library("abc");

# compute Bhattacharyya distance
Bhattacharyya.distance <- function(original = a,b)
{
  bb <- matrix(nrow=nrow(b),ncol=ncol(b),rep(original,nrow(b)),byrow=TRUE);
  logs <- -log(rowSums((b*bb)^0.5));
  return(logs);
}

Hellinger.distance <- function(original = a, b)
{
  bb <- matrix(nrow=nrow(b),ncol=ncol(b),rep(original,nrow(b)),byrow=TRUE);
  logs <- ((rowSums((bb^0.5-b^0.5)^2))^0.5)/(2^0.5);
  return(logs);
}

Mahalanobis.distance <- function(original = a, b)
{
  vars <- apply(b,2,var);
  bb <- matrix(nrow=nrow(b),ncol=ncol(b),rep(original,nrow(b)),byrow=TRUE);
  bbvars <- matrix(nrow=nrow(b),ncol=ncol(b),rep(vars,nrow(b)),byrow=TRUE);
  logs <- rowSums((b - bb)^2/bbvars)^0.5;
}

# now, for each of the neural network replicates, compute the Bhattachardyya distance. Return the sum
compute.distance <- function(simulated.ss, observed.ss, nen, n.models, distance.function)
{
  simulated.ss.by.nn <- matrix(nrow=nrow(simulated.ss),ncol=nen, rep(0,nrow(simulated.ss)*nen));
  
  for(nn in 1:nen)
  {
    sequence <- seq(from=(1+n.models*(nn-1)),to = (n.models+n.models*(nn-1)),by=1);
    simulated.ss.by.nn[,nn] <- distance.function(simulated.ss[,sequence],original = observed.ss[sequence]);
  }
  return(rowSums(simulated.ss.by.nn));
}

# now, for each of the neural network replicates, compute the Bhattachardyya distance. Return the sum
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


weight.networks <- function(models, simulated.ss, nn, samples.by.model)
{
  # models is a vector with all the models by sample
  # simulated.ss is the matrix with the output of the neural networks to the simulated replicates
  # nn is the number of neural networks  
  # samples.by.model is the number of samples we want to use for estimating the weight
  # for each network and each model, take samples.by.model, compute the bhattacharyya.distance for each simulation and retain the posterior of the model of interest
  levels.models <- as.numeric(levels(as.factor(models)));
  n.models <- length(levels.models);
  weights.nn <- rep(0, nn);
  for(m in 1:n.models)
  {
    ids.model <- sample(which(models==levels.models[m]),samples.by.model);
    for(n in 1:nn)
    {
      sequence <- seq(from=(1+n.models*(n-1)),to = (n.models+n.models*(n-1)),by=1);
      for(s in 1:samples.by.model)
      {
        print(c(n,m,s));
        distance <- compute.distance(simulated.ss[-ids.model[s],sequence], simulated.ss[ids.model[s],sequence], 1, n.models);
        posterior.abc <- models[distance <= quantile(distance,1000/(length(distance)))];
        weights.nn[n] <- weights.nn[n] + sum(posterior.abc==levels.models[m])/1000;
      }      
    }
  }
  return(weights.nn);
}

setwd("path/to/your/folder")

data.t <- read.table(file="model_predictions.txt",header=F)
simulated.ss <- as.matrix(data.t[,2:ncol(data.t)]);
model_names <- as.factor(as.character(data.t[,1]));
model_ids <- rep(-1,length(model_names));
mods <- levels(model_names);
for(i in 1:length(mods))
{
  model_ids[model_names==mods[i]] <- i;
}

mean.nn <- compute.mean.nn(simulated.ss, 10, 6); #last number = number of models

# This you will have to change it for each population you test
observed.t <- read.table(file=paste("observed.abcmodel",sep=""),header=F); #file with observed output
observed.ss <- as.matrix(observed.t[2:ncol(observed.t)]);
mean.observed.nn <- compute.mean.nn(observed.ss,10,6); #last number = number of models


res <- postpr(mean.observed.nn, model_ids,mean.nn,tol=1000/nrow(mean.nn),method="mnlogistic")#interchange with "rejection" to see the difference.
summary(res)
mods


confussion.matrix <- matrix(nrow=length(mods), ncol=length(mods),rep(0,length(mods)*length(mods)));
rownames(confussion.matrix) <- as.character(mods);
colnames(confussion.matrix) <- as.character(mods);


for(m in 1:length(mods))
{
  ids <- which(model_ids==m);
  print(m);
  for(rep in 1:100) #the number of simulations tested
  {
    use.id <- sample(ids,1);
    mean.ss.observed.nn <- mean.nn[use.id,];
    res <- postpr(mean.ss.observed.nn, model_ids[-use.id],mean.nn[-use.id,],tol=1000/nrow(mean.nn),method="mnlogistic");
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


