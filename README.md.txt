ABC_DL
The paper “Approximate Bayesian computation with a Deep Learning algorithm (ABC-DL) supports at least a third archaic introgression common to all Asian and Oceanian human populations” introduces a methodology that combines the statistical approach Approximate Bayesian Computation (ABC) with Deep Learning (DL). The idea is that we can use DL to train a neural network to predict a demographic model or a parameter from a demographic model, and then use the ABC framework to estimate the posterior probability of the parameter/model of the observed data. In order to show that the proposed methodology works, we implemented a pipeline in JAVA/R and applied it to the identification of archaic introgression in Asian and Oceanian human populations.

In the proposed framework, implementing a particular model comparison/parameter estimation requires JAVA and R programming skills and it is obvious that there could be other better implementations using other languages/packages. This is to say that by no way this document should be considered as “how to do ABC-DL”, but just to use the implementation that we have done to a very particular problem –comparing complex demographic models that include archaic introgression.

In order to use the JAVA package, you will need the SDK1.8 and JRI1.8, as well as a JAVA editor to import the src code. The project has been created using Netbeans (www.netbeans.org) and uses the external package EncogV3.4 (https://github.com/encog). It also requires fastSimcoal2 software (http://cmpg.unibe.ch/software/fastsimcoal2/) to be available for conducting the simulations. We assume that the reader has the basic knowledge to import the project in his or her favorite editor and we will not describe how to do that.

We have included a pdf manual describing the steps to conduct the analyses that we proposed in that paper. In order to exemplify how to run the ABC-DL, we have included a step-by-step example for the comparison of two models that include archaic samples and archaic introgression.

Please, refer to manual.pdf and contact oscar.lao@cnag.crg.eu in case of any doubt/problem.

Organization of the project:

Manual ABC_DL.pdf The manual explaining how to develop the JAVA pipeline and to conduct the ABC analyses.

abcModel.R The R script to run the ABC model comparison.

abcParameter.R The R script to run the ABC parameter comparison.

ABC_DL_Example_Project A folder with the example described in the Manual.

ABC_DL_Suite The JAVA Netbeans project including the source code.

jar libraries JAVA libraries that must be imported to the ABC_DL_Suite package