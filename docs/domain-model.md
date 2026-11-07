# Domain model

## Antenna

The antenna has a position and a facing direction. Its position is used
to measure the distance to each robot. If two robots are equally far
away, its facing direction determines where the clockwise sweep starts.

## Robot

A robot has a position and a facing direction. Turning changes its
facing direction while it stays on the same square. When it plays
Move 1, it attempts to move one square in that direction.

Each robot tracks the number of its last completed checkpoint, starting
at 0. At the end of a register, the robot completes a checkpoint only
if its number is exactly one higher than the last one completed.

## Position

A position identifies one square on the board by its x and y coordinates.
Both robots and antennas use this type. The distance between two positions
is the number of horizontal and vertical steps between them.

## Direction

Robots and antennas can face north, east, south, or west. The `Direction`
enum defines these four values. Using the same type for both keeps direction
names consistent and catches misspelled enum constants at compile time.

## BoardCell

A board cell can contain multiple board elements. For example, a wall
and a laser can share the same cell. Each element stores the details
needed for its behavior, such as which edge a wall blocks or which
direction a laser fires.

## Board

The board stores the layout of its cells. Given a position, it can
return the cell at those coordinates. Each cell holds the board
elements at that location. To check for a wall between two adjacent cells, the board checks the
exit edge of the current cell and the entry edge of the next cell.
A wall on either edge blocks the crossing.

The board provides the final checkpoint number for the selected map. The board provides the position of the appropriate restart space when a robot reboots.

## Player

Each player controls one robot and has their own programming deck and
discard pile. When the player needs to draw a card and their deck is
empty, they shuffle their discard pile to form a new deck.
A player's hand can contain multiple copies of the same card type.

The player controls changes to its hand and exposes a read-only view
of the cards to callers.

## Program

A program has exactly five numbered registers. Each register can hold
at most one card. The program is ready when all five registers are
filled. Robots execute the cards in register order. Each program owns its five registers. Changing a card in one program
leaves the registers in other programs unchanged. Placing a card in a register moves one copy of that card from the
player's hand into the selected register.

If a card placement is rejected, both the player's hand and the
selected register keep their previous contents.

## Movement

A robot stores its position and facing direction. The board identifies
walls between adjacent cells. To resolve a push, the movement rules
check the robots in the path and the edges they would cross. If a wall
blocks any robot in the chain, all robots in the chain stay in place.

## Board lasers

A robot can move into a laser's path. When board lasers fire, the
player takes one SPAM card for each beam that hits their robot and
places those cards in their discard pile.

## Game

The game keeps track of the robots in play. To find the robot on a
given square, it checks each robot's current position. This keeps
each robot's location in one place as it moves across the board.

The game keeps a list of players. Each player controls one robot.
To find the robot on a given square, the game checks the position
of each player's robot.

The selected board determines how many checkpoints a robot must complete.
When a robot completes a checkpoint, the game checks whether it was the
final one on that board. The first robot to complete it wins.

The game tracks the number of cards left in the shared supply for each
damage card type. All players draw damage cards from this supply.

The shared damage supply uses a `Map<DamageCardType, Integer>` to track
how many cards of each damage type remain.

## Board elements

A board cell can hold different kinds of board elements. A wall stores
the edges it blocks. A laser stores its firing direction and beam count.
Both are represented by the common type `BoardElement`.
Walls block movement when a robot tries to cross an edge. Lasers and
gears apply their effects during board activation.