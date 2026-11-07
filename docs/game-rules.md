# RoboRally game rules

## Programming deck

Each player starts with a separate deck of 20 programming cards.
All six starting decks have the same card distribution.

| Card | Copies per deck |
|---|---:|
| Move 1 | 5 |
| Move 2 | 3 |
| Move 3 | 1 |
| Right Turn | 3 |
| Left Turn | 3 |
| Backup | 1 |
| Power Up | 1 |
| Again | 2 |
| U-Turn | 1 |
| **Total** | **20** |

## Programming and Activation

During a standard programming phase, each player draws nine cards, places one card in each of five numbered registers, and discards the remaining four cards. The order matters because each instruction uses the robot's position and facing at the moment it runs.

During activation, all robots resolve register 1 in priority order. Once their cards have resolved, board elements activate in the prescribed order. Play then advances to register 2 and repeats through register 5. A wall can block a movement instruction. Board effects still apply for that register.

To determine priority, count the horizontal and vertical steps from the
antenna to each robot. The closest robot acts first. If robots are equally
far from the antenna, start in the direction the antenna dish faces and
sweep clockwise. The first tied robot reached by the sweep acts first.

When a player needs to draw a card and their programming deck is empty,
they shuffle their discard pile to form a new deck. Any SPAM cards in
that pile become part of the new deck and can be drawn into the player's hand.

When a SPAM card activates, return it to the SPAM pile. Draw the top card
from the player's programming deck and play it immediately in the same register.

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

### Pushing a robot into a pit

A pit lies one square east of Robot B, with no wall between them.
Robot A plays `Move 1` and pushes B into the pit. A ends the move
on B's former square, and B reboots.

## Reboot

A robot reboots when it falls into a pit, moves off the board, or a card
instructs it to reboot. Place the robot on its designated restart space.
The player puts two SPAM damage cards in their discard pile. The robot
skips its remaining registers for this round.

## Checkpoints

A robot must reach the checkpoints in numerical order. A checkpoint
counts when the robot ends a register on it and it is the next one
in sequence.

For example, a robot that visits checkpoint 2 before checkpoint 1
must return to checkpoint 2 after completing checkpoint 1.
The first robot to complete the final checkpoint wins.


## Damage cards

All players draw damage cards from a shared supply.

| Card | Starting supply |
|---|---:|
| SPAM | 38 |
| Virus | 18 |
| Trojan Horse | 12 |
| Worm | 6 |

If a damage pile is empty, the player chooses a card from another
available damage pile. For example, if only one SPAM card remains
when a robot reboots, the player takes that card and chooses one
other damage card. Both cards go into the player's discard pile.

Source: [RoboRally Game Guide, p. 15](https://media.wizards.com/2017/rules/roborally_rules.pdf#page=15)

## Scope

The first playable version covers the base game, including programming
cards, damage cards and board effects. Upgrade cards are an optional
extension.

