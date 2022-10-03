This directory is used for mapping entities' models to proper animation positions while they are riding a skateboard.
The entity's type can be specified by its registry name or via an entity_type tag.
animation_speed_modifier is how fast certain animations, like the pedalling, are played for this entity.
animation_strength_modifier is similar, except it determines how far the parts will swing back and forth.

"Humanoid" (ex. player, zombie, enderman, skeleton etc) mobs are already hard coded by default.

Note that this system does respect the parenting of parts, to get a part to move only use its parent.
All of the tags refrenced in this by default should not include "fancy" entity models like those in Ice and Fire, Mowzie's Mobs and Alex's Mobs.
Create seperate mappings for those instead, since they use separate modelling/animation techniques than vanilla.