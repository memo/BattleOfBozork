# uncomment to install ggplot
#install.packages("ggplot2")
library(ggplot2)

# plots xy positions
p = ggplot()

for(game in seq(0,4)){
name1 = paste("Game_",game,"_player0.csv",sep="")
name2 = paste("Game_",game,"_player1.csv",sep="")
data_1 <- read.table(name1,header=TRUE, sep=',' )
data_2 <- read.table(name2,header=TRUE, sep=',' )

p = p + geom_path(data=data_1, aes(x=pos_x, y=pos_y, colour="Pos player 1"))
p = p + geom_path(data=data_2, aes(x=pos_x, y=pos_y, colour="Pos player 2"))
}

print(p)
head(data_1)

# plots xy positions
p = ggplot()
p = p + geom_path(data=data_1, aes(x=pos_x, y=pos_y, colour="Pos player 1"))
p = p + geom_path(data=data_2, aes(x=pos_x, y=pos_y, colour="Pos player 2"))


# plots thrust
p = ggplot()
p = p + geom_path(data=data_1, aes(x=frame, y=thrust, colour="Thrust player 1"))
p = p + geom_path(data=data_2, aes(x=frame, y=thrust, colour="Thrust player 2"))
print(p)

# plots turn
p = ggplot()
p = p + geom_path(data=data_1, aes(x=frame, y=turn, colour="Turn player 1"))
p = p + geom_path(data=data_2, aes(x=frame, y=turn, colour="Turn player 2"))
print(p)

# plots scores
p = ggplot()
p = p + geom_line(data=data_1, aes(x=frame, y=score, colour = "Score player 1"))
p = p + geom_line(data=data_2, aes(x=frame, y=score, colour = "Score player 2"))
print(p)

# plots bullets
p = ggplot()
p = p + geom_line(data=data_1, aes(x=frame, y=num_bullets, colour = "Score player 1"))
p = p + geom_line(data=data_2, aes(x=frame, y=num_bullets, colour = "Score player 2"))
print(p)