# RoboRally game rules

## Programming and Activation

During a standard programming phase, each player draws nine cards, places one card in each of five numbered registers, and discards the remaining four cards. The order matters because each instruction uses the robot's position and facing at the moment it runs.

During activation, all robots resolve register 1 in priority order. Once their cards have resolved, board elements activate in the prescribed order. Play then advances to register 2 and repeats through register 5. A wall can block a movement instruction. Board effects still apply for that register.

### Example: turning on a clockwise gear

A robot faces north. The square immediately north contains a clockwise gear. Assume the surrounding squares are clear and no other effects intervene.

| Register | Card | After the card | After board effects |
| --- | --- | --- | --- |
| 1 | Move 1 | The robot moves north onto the gear. | The gear turns it east. |
| 2 | Turn Right | The robot turns south in place. | It still occupies the gear, which turns it west. |
| 3 | Move 1 | The robot moves one square west, leaving the gear. | It remains facing west. |

The same gear affects the robot in two consecutive registers because the robot occupies that square both times the gear activates.

## Pushing and walls

Robot A faces east and plays `Move 1`. Robot B occupies the square
immediately east of A. The next square beyond B is empty.

- If a wall blocks B from entering the empty square, both robots stay
  in their original positions.
- If the path is clear, A pushes B one square east and moves into
  the square B just left.
