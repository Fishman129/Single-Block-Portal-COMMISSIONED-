I was commissioned to build this small framework that adds a block that acts exactly like a nether portal, so that it can be customized to add other dimensions.

---
Summary:
Overworld <<->> Nether teleport (8:1)

Reuses nearby portals (radius 96)

Creates a new portal if none exist nearby

---
Java classes:

SinglePortalBlock.java

SinglePortalBlockEntity.java

ModBlockEntities.java

ModBlocks.java

PortalTeleporter.java

PortalLocator.java

PortalPlacer.java

---
Client-only (overlay)

InGameHudMixin.java

singleportal.client.mixins.json

Resources (overlay texture)

assets/<your_modid>/textures/misc/single_portal_overlay.png

(If you use a sprite sheet overlay, also add:)

single_portal_overlay.png.mcmeta (optional if using manual UV animation, I used manual cuz I never got the mcmeta working but you can figure it out lol)

---
Call:

ModBlockEntities.register() during mod initialization, not lazily.

If you skip this, you will crash on placement took me a while to figure out (“intrusive holders” registry error).

---
The portal block has a BlockEntity storing:

destination dimension id

destination BlockPos

Teleporting does:

If link exists + destination portal still exists -> teleport there

Otherwise compute scaled coords -> find nearest portal or place one -> then write link on both portals

---
Extending to additional dimensions

Right now the destination logic is in:

PortalTeleporter.getDestination(fromWorld)

---
Scale ratios:

Overworld -> Nether : multiply by 1/8

Nether -> Overworld : multiply by 8

To support different ratios per dimension replace the single scale with a lookup:

(fromDim, toDim) -> scale factor

Then apply:

targetX = floor((fromPortalX + 0.5) * scale)
targetZ = floor((fromPortalZ + 0.5) * scale)


(Using portal block position gives consistent results.)

---
Nearby portal radius:

search radius = 96 blocks (configurable constant)

Change in:

PortalTeleporter.SEARCH_RADIUS

---
For your overlay:

Add the mixin JSON to fabric.mod.json:

"mixins": [
  "<your_client_mixins>.json"
]

Put InGameHudMixin in your client sourceset.

Add the overlay PNG at:
assets/<your_modid>/textures/misc/single_portal_overlay.png

Animated overlay:


Im using a vertical sprite sheet (16×512) but Im guessing youll figure this out



