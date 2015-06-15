# uncomment to install ggplot
#install.packages("ggplot2")
library(ggplot2)

data_1 <- read.table('data_test1_player0.csv',header=TRUE, sep=',' )
data_2 <- read.table('data_test1_player1.csv',header=TRUE, sep=',' )

head(data_1)

plot(data_1$frame, data_1$pos_x,type='l', xlab="frame", ylab="pos_x")
plot(data_1$pos_x, data_1$pos_y,type='l', xlab="x", ylab="y")


plot_overlap <- function(ox,sx,sy)
{
  vel_scaled = data_orig_xy 
  vel_scaled$vel = vel_scaled$vel * sy
  vel_scaled$time = vel_scaled$time * sx + ox
  p = ggplot()
  p = p + geom_line(data=data_xy_stripped, aes(x=time, y=vel, colour="Reconstructed")) 
  p = p + geom_line(data=vel_scaled, aes(x=time, y=vel, colour="Scaled and Aligned Original")) 
  p = p + labs(x="Time (seconds)",y = "Velocity (Screen Units per Second)", colour="")
  return(p) 
}

plot_overlap(0.1,0.68,1.6)
