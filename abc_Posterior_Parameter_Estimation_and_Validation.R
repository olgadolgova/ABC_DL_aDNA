# Common to every method

rm(list=ls());
library("abc");
library("bayestestR");
library("genefilter");
library("ggplot2");
# simulated data
abc_folder <-"path\\to\\your\\folder\\"; 
abc_sims <- paste(abc_folder,"Model_B_replication_parameter_for_ABC.txt", sep=""); # change to the name of your file
obs <- read.table(file=paste(abc_folder,"Model_B_observed_parameter_for_abc.txt",sep=""),header=T); # change to the name of your file
data.tt <- read.table(file=abc_sims,header=T);
number.of.replicates.by.parameter <- 10;

##############################################################################################################
#
# A small script to run the abc function from the abc package
#
# Requires
# target, the observed summary statistics in the data
# param, the parameter we want to estimate from the simulated data
# sumstat, the same summary statistics as the observed data, for each simulation
# tol, the percentage of accepted sims to sample the posterior distribution
# method: the ABC method for acceptance of the simulations (the same as in the method abc)
##############################################################################################################
abc.oscar <- function(target,param, sumstat, tol, method)
{
  abc.result <- matrix(nrow=tol*nrow(sumstat),ncol=ncol(param));
  colnames(abc.result) <- colnames(param);
  for(col in 1:ncol(param))
  {
    transformation <- c("logit");
    boundaries <- matrix(nrow= 1,ncol=2);
    boundaries[1,1] <- min(param[,col]);
    boundaries[1,2] <- max(param[,col]);
    target.s <- target[col];
    sumstat.s <- sumstat[,col];
    run.abc <- abc(target.s,param[,col],sumstat.s,tol = tol,method=method, transf=transformation, logit.bounds = boundaries);    
    if(method=="rejection")
    {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$unadj.values),ncol=ncol(param));
      }
      abc.result[,col] <- run.abc$unadj.values;
    }
    else
    {
      if(col==1)
      {
        abc.result <- matrix(nrow=nrow(run.abc$adj.values),ncol=ncol(param));
      }     
      abc.result[,col] <- run.abc$adj.values;
    }
  }
  return(abc.result);
}



##############################################################################################
#
#
# ABC analyses
#
##############################################################################################

result.matrix <- matrix(nrow=1000,ncol=(ncol(data.tt)/(number.of.replicates.by.parameter+1)));

nams <- c();


####################################CI and HDI for prior##############################################
for(i in 1:ncol(result.matrix))
{
  print(i);
  parameter <- as.matrix(data.tt[,(11*(i-1)+1)]);
  h <- hdi(parameter);
  print(c(nams[i], quantile(parameter,probs=c(0.025,0.975)),h$CI_low,h$CI_high));
}
######################################################################################################


for(param in 1:ncol(result.matrix))
{
  print(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  nams <- c(nams, names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  sim.param <- data.tt[,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  start <- ((number.of.replicates.by.parameter+1)*(param-1)+2);
  end <- ((number.of.replicates.by.parameter+1)*(param-1)+2+(number.of.replicates.by.parameter-1));
  start.obs <- ((number.of.replicates.by.parameter)*(param-1)+1);
  end.obs <- ((number.of.replicates.by.parameter)*(param-1)+1+(number.of.replicates.by.parameter-1));
  if(start!=end)  {
    ss.pred <- rowMeans(data.tt[,start:end]);
    observed <- mean(as.numeric(obs[1,start.obs:end.obs]));
  } else {
    ss.pred <- data.tt[,start];
    observed <- obs[1,start.obs];
  }
  result.matrix[,param] <- abc.oscar(observed,as.matrix(sim.param),as.matrix(ss.pred),1000/nrow(data.tt),"loclinear");
}

colnames(result.matrix) <- nams;


CI <- matrix(nrow=ncol(result.matrix),ncol=4);
colnames(CI) <- (c("0.025CI","0.975CI","HDI_89_low", "HDI_89_high"));
for(c in 1:ncol(result.matrix))
{
  CI[c,1:2] <- quantile(result.matrix[,c],probs=c(0.025,0.975));
  h <- hdi(result.matrix[,c]);
  CI[c,3] <- h$CI_low;
  CI[c,4] <- h$CI_high;
}

write.table(result.matrix, file=paste(abc_folder,"results.txt",sep=""), row.names = FALSE);

result <- data.frame(colMeans(result.matrix), apply(result.matrix,2,median), apply(result.matrix,2,half.range.mode),CI);

rownames(result) <- nams;

write.table(result,file=paste(abc_folder, "summary_statistics.txt",sep=""));

# print the histograms

pdf(file=paste(abc_folder, "Parameter_Histogram.pdf",sep=""),onefile = TRUE);


for(i in 1:ncol(result.matrix))
{
  print(i);
  parameter <- as.matrix(data.tt[,(11*(i-1)+1)]);
  carrots <- data.frame(parameter = parameter)
  cukes <- data.frame(parameter = result.matrix[,i])
  
  # Now, combine your two dataframes into one.  
  # First make a new column in each that will be 
  # a variable to identify where they came from later.
  carrots$distribution <- 'prior'
  cukes$distribution <- 'posterior'
  
  # and combine into your new data frame vegLengths
  vegLengths <- rbind(carrots, cukes)  
  
  tplot <- ggplot(vegLengths, aes(parameter, fill = distribution)) +
    geom_histogram(alpha = 0.5, aes(y = ..density..), position = 'identity') + theme_classic() + labs(x = nams[i]);
  
  print(tplot);
}
dev.off();


write.table(file=paste(abc_folder, "posterior.txt",sep=""),result);   


#########################################################################################
#
#
# Analysis to see how well the DL and ABC DL is working
#
#

#########################################################################################
#
# DL
#
#########################################################################################


#########################################################################################
#
# Estimate correlation between observed parameter and DL predicted parameter
#
#
#########################################################################################


# compute the correlation between the mean predicted value over all the number.of.replicates.by.parameter DLs and the parameter used in the simulation
estimate.cor.param.dl.prediction <- function(param, number.of.replicates.by.parameter, data.tt)
{
  print(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  # where do the DL replicates of the current parameter start and end, given that we know the number.of.replicates.by.parameter
  start <- ((number.of.replicates.by.parameter+1)*(param-1)+2);
  end <- ((number.of.replicates.by.parameter+1)*(param-1)+2+(number.of.replicates.by.parameter-1));
  # "observed simulated parameters" the value of the simulated parameter that is used as observed
  obs.sim.param <- data.tt[,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  # mean scaled of the parameter predicted by DL
  sim.param <- rowMeans(data.tt[,start:end]);
  # return the correlation
  return(cor.test(obs.sim.param, sim.param, method = "spearman"));
}

result.matrix <- matrix(nrow=(ncol(data.tt)/11), ncol = 2);

colnames(result.matrix) <- c("spearman.correlation.between.sim.parameter.mean.predicted.dl","p_value");

nams <- c();

for(param in 1:nrow(result.matrix))
{
  nams <- c(nams,names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  e <- estimate.cor.param.dl.prediction(param,10,data.tt);
  result.matrix[param,1] <- e$estimate;
  result.matrix[param,2] <- e$p.value;
}

rownames(result.matrix) <- nams;

write.table(file=paste(abc_folder, "Spearman_cor.txt",sep=""),result.matrix)

##################################
# Spearman correlation graphs    #
##################################

pdf(file= paste("Ghostapanuli_medium_intro_lessNe_correlations.pdf",sep=""));

for(t in seq(from=1,to=ncol(result.matrix),by=4))
{
  par(mfrow=c(1,1));
  par(mfrow=c(2,2));  
  cosa <- min((t+3),(ncol(result.matrix)));
  for(i in t:cosa)
  {
    print(c(i,colnames(result.matrix)[i]));
    ss <- as.matrix(data.t[,(11*(i-1)+2):(11*(i-1)+11)]);
    sss <- as.matrix(rowMeans(ss));
    parameter <- as.matrix(data.t[,(11*(i-1)+1)]);
    sss <- sss[parameter[,1]!=0,];
    parameter <- parameter[parameter[,1]!=0,];       
    corre <- cor(parameter,sss);
    plot(parameter[1:5000], sss[1:5000], main=paste("Correlation =",corre, sep=" "), xlab = paste("simulated",colnames(result.matrix)[i],sep=" "), ylab=paste("predicted",colnames(result.matrix)[i],sep=" "));
    abline(lm(sss[1:5000] ~ parameter[1:5000]),col="red");
  }
}
dev.off();

par(mfrow=c(1,1));


correlations <- rep(NA,(ncol(result.matrix)));
names(correlations) <- colnames(result.matrix);
for(i in 1:ncol(result.matrix))
{
  ss <- as.matrix(data.t[,(11*(i-1)+2):(11*(i-1)+11)]);
  sss <- as.matrix(rowMeans(ss));
  parameter <- as.matrix(data.t[,(11*(i-1)+1)]);
  sss <- sss[parameter[,1]!=0,];
  parameter <- parameter[parameter[,1]!=0,];       
  correlations[i]<- cor(parameter,sss);
}


###################################################################################
#
#
# ABC performance
#
#
###################################################################################
#
#   Check the performance of the ABC-DL using Excoffier Bayesian Analysis of an Admixture Model With Mutations and Arbitrarily Linked Markers
#
###################################################################################


# compute the factor k of the parameter param and the number of replicates by parameter. 
# k is the number of times that the mean of the posterior is different from the prior. 
# For example, 2 means factor.2 as the one defined by Excoffier
factor.k <- function(k, param, number.of.replicates.by.parameter, n, data.tt)
{
  print(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  # pick n sims at random that will be the "observed" data
  s <- sample(1:nrow(data.tt), n);
  # for each simulation s, do ABC. Store in sim.res the mean of the posterior
  sim.res <- matrix(nrow=n,ncol=2);
  colnames(sim.res) <- c("mean","mean_if_prior");
  # simulated parameters that will be used in the abc
  sim.param <- data.tt[-s,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  # where do the DL replicates of the current parameter start and end, given that we know the number.of.replicates.by.parameter
  start <- ((number.of.replicates.by.parameter+1)*(param-1)+2);
  end <- ((number.of.replicates.by.parameter+1)*(param-1)+2+(number.of.replicates.by.parameter-1));
  ss.pred <- rowMeans(data.tt[-s,start:end]);
  # "observed simulated parameters" the value of the simulated parameter that is used as observed
  obs.sim.param <- data.tt[s,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  # now, for each simulation, compute the posterior and the centrality statistics. Store the difference of each centrality statistic with the parameter used in the simulation
  for(sim in 1:length(s))
  {
    # parameter used in the simulation
    obs.param <- obs.sim.param[sim];
    # observed summary statistic. It is the mean over all the DL predictions
    observed <- mean(as.numeric(data.tt[s[sim],start:end]));
    # sampled values from the posterior
    res <- abc.oscar(observed,as.matrix(sim.param),as.matrix(ss.pred),1000/(nrow(data.tt)-n),"loclinear");
    # mean divided by the value of the parameter used in the simulation
    sim.res[sim,1] <- mean(res)/obs.sim.param[sim];
    # assume that the posterior is, in fact, sampled from the prior. The mean of the posterior is sampled from 1000 sims 
    sim.res[sim,2] <- mean(sim.param[sample(1:length(sim.param), n)])/obs.sim.param[sim];
  }
  # factor k is the number of times that the estimated mean falls within 80% to 125% of the value used in the simulation
  factor2.result <- mean(sim.res[,1] > (1/k) & sim.res[,1] < k);
  factor2.prior  <- mean(sim.res[,2] > (1/k) & sim.res[,2] < k);
  # return the factork
  return(c(factor2.result, factor2.prior));
}  


# matrix to store the results for each 
factor2.result <- matrix(nrow=(ncol(data.tt)/11),ncol=2);

n <- c();

for(param in 1:(ncol(data.tt)/11))
{
  n <- c(n,(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]));
}

for(param in 1:(ncol(data.tt)/11))
{
  factor2.result[param,] <- factor.k(1.25, param, 10, 1000, data.tt);
}

rownames(factor2.result) <- n;
write.table(file = paste(abc_folder,"factor1.25.txt",sep=""),factor2.result)

###################################################

#                KL-distance                      #

###################################################


# Compute Kullback-Leibler divergence between the posterior and the prior of the variable.

# How it works? Divide the continuous space of the variable into categories using the hist function.

# By doing this, we transform the continuous variable in a discrete variable.

# Assign each element of the posterior and the prior to the categories of this discrete variable.

# Compute the frequency of each category in the posterior and prior.

# Compute sum(f_posterior*log(f_posterior/f_prior))

# Conditions: the length of the posterior MUST BE the same as in the prior


kl_divergence <- function(posterior, prior)
{
  #if(length(posterior)!=length(prior))
  #{
  # print("the number of elements of the posterior must be the same as the prior. Do a subsampling of the prior so the number of elements is the same as in the posterior.");
  #  return(NaN);
  #}
  # merge the values of the posterior and prior. Do a histogram to identify the breakpoints
  breaks <- hist(c(posterior,prior), plot = F)$breaks;
  # now assign each observation from the prior and posterior to one of the categories defined by the breaks
  posterior.discrete <- cut(posterior,breaks);
  prior.discrete <- cut(prior,breaks);
  # frequency of each break in posterior and discrete
  f.posterior <- table(posterior.discrete);
  f.prior <- table(prior.discrete);
  # add 0.5/length(posterior) to each position of f.posterior and f.prior that have a 0 count.
  f.posterior[f.posterior==0] <- 0.5/length(posterior);
  f.prior[f.prior==0] <- 0.5/length(prior);
  #now compute the frequency
  f.posterior <- f.posterior/sum(f.posterior);
  f.prior <- f.prior/sum(f.prior);
  # compute kl_divergence
  kl <- sum(f.posterior*log(f.posterior/f.prior));
  # return the kl divergence
  return(kl);
}

kl_table <- matrix(nrow=ncol(result.matrix),ncol=2,NA);

rownames(kl_table) <- colnames(result.matrix);

for(i in 1:ncol(result.matrix))
{
  print(i);
  parameter <- as.matrix(data.tt[,(11*(i-1)+1)]);
  parameter <- as.matrix(parameter[parameter[,1]>0,]);
  kl_table[i,1] <- kl_divergence(parameter[,1], result.matrix[,i]);
  p_val <- 0;
  for(rep in 1:1000)
  {
    para <- sample(parameter[,1],length(result.matrix[,i]));
    kl_res <- kl_divergence(parameter[,1], para);
    if(kl_res >= kl_table[i,1])
    {
      p_val <- p_val + 1;
    }
  }
  kl_table[i,2] <- p_val/1000;
}

write.table(file="relaxed_KL_logit_divergence between prior_posterior_pvalue.txt", data.frame(colnames(result.matrix), kl_table), row.names = F, col.names = F);



###################################################################################
#
# compute which centrality statistic (mean, median, HRM mode) better fits the parameter
#
#
###################################################################################


# compute using n simulated samples randomly sampled from data.tt the posterior distribution and return the mean, median and mode
estimate.performance.centrality.statistics <- function(param, n, number.of.replicates.by.parameter, data.tt)
{
  print(names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  # pick 1000 sims at random that will be the "observed" data
  s <- sample(1:nrow(data.tt), n);
  # for each simulation s, do ABC. Store in sim.res the mean, median, mode and var of posterior, the last scaled by the sd of prior
  sim.res <- matrix(nrow=n,ncol=4);
  colnames(sim.res) <- c("mean","median","mode", "var_ratio");
  # simulated parameters that will be used in the abc
  sim.param <- data.tt[-s,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  # average distance between any two values of the parameter if they are taken from the prior distribution (the variance)
  var.param <- var(sim.param);
  # where do the DL replicates of the current parameter start and end, given that we know the number.of.replicates.by.parameter
  start <- ((number.of.replicates.by.parameter+1)*(param-1)+2);
  end <- ((number.of.replicates.by.parameter+1)*(param-1)+2+(number.of.replicates.by.parameter-1));
  ss.pred <- rowMeans(data.tt[-s,start:end]);
  # "observed simulated parameters" the value of the simulated parameter that is used as observed
  obs.sim.param <- data.tt[s,((number.of.replicates.by.parameter+1)*(param-1)+1)];
  # now, for each simulation, compute the posterior and the centrality statistics. Store the difference of each centrality statistic with the parameter used in the simulation
  for(sim in 1:length(s))
  {
    # parameter used in the simulation
    obs.param <- obs.sim.param[sim];
    # observed summary statistic. It is the mean over all the DL predictions
    observed <- mean(as.numeric(data.tt[s[sim],start:end]));
    # sampled values from the posterior
    res <- abc.oscar(observed,as.matrix(sim.param),as.matrix(ss.pred),1000/(nrow(data.tt)-n),"loclinear");
    # mean
    sim.res[sim,1] <- mean(res);
    # median
    sim.res[sim,2] <- median(res);
    # mode. Use HRM
    sim.res[sim,3] <- half.range.mode(res);
    # sd scaled by the sd of the prior
    sim.res[sim,4] <- var(res)/var.param;
  }
  # return the parameter of the simulation and the mean, median and mode of the posterior for each of the 1,000 simulations
  return(cbind(obs.sim.param, sim.res));
}

# results to be stored: the mean of the ratio between the 95CI of posterior/95CI prior, the mean diference between the observed parameter and the mean, median, mode of the posterior, wilcox.test between mean - median, mean - mode and median - mode
result.matrix <- matrix(nrow=(ncol(data.tt)/11), ncol = 7);

colnames(result.matrix) <- c("mean_varposterior_divided_var_prior", "mean_diff_observed_mean_posterior","mean_diff_observed_median_posterior","mean_diff_observed_mode_posterior", "mean_median", "mean_mode", "median_mode");

nams <- c();

for(param in 1:nrow(result.matrix))
{
  nams <- c(nams,names(data.tt)[((number.of.replicates.by.parameter+1)*(param-1)+1)]);
  res <- estimate.performance.centrality.statistics(param, 1000, 10, data.tt = data.tt);
  # mean variance of the posteriors scaled by the variance of the priors
  result.matrix[param,1] <- mean(res[,5]);
  # mean difference between the observed and estimated mean of the posterior
  result.matrix[param,2] <- mean(res[,1]-res[,2]);
  # mean difference between the observed and estimated median of the posterior
  result.matrix[param,3] <- mean(res[,1]-res[,3]);  
  # mean difference between the observed and estimated mode of the posterior
  result.matrix[param,4] <- mean(res[,1]-res[,4]);
  # pvalue of the comparison between mean of the posterior and median of the posterior
  result.matrix[param,5] <- mean(abs(res[,1]-res[,2]) < abs(res[,1]-res[,3]));
  # pvalue of the comparison between mean of the posterior and median of the posterior
  result.matrix[param,6] <- mean(abs(res[,1]-res[,2]) < abs(res[,1]-res[,4]));
  # pvalue of the comparison between mean of the posterior and mode of the posterior
  result.matrix[param,7] <- mean(abs(res[,1]-res[,3]) < abs(res[,1]-res[,4]));    
}

rownames(result.matrix) <- nams;

write.table(file="path\\to\\the\\file.txt",result.matrix);




