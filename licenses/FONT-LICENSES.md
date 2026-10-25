# Font licensing notes

## Poppins Light
Poppins is distributed under the SIL Open Font License 1.1. The full license is included as `Poppins-OFL.txt`.

## Moderniz
Moderniz is distributed by its designer Eko Bimantara as a free font for personal and commercial use. The supplied file is used unmodified.

## HVD Comic Serif Pro
The upstream HVD Fonts license permits use but states that the raw typeface file may not be redistributed. For that reason the raw HVD font is deliberately **not committed to this public repository**.

The Android CI workflow retrieves the public upstream package at build time and verifies SHA-256 against the user's supplied file:

`b15f185f6743d2ba99a378cb613216592b1e668f17ed769c1f3fa6066ef9a23f`

If the upstream bytes do not match exactly, the build fails rather than silently substituting another font.

The application itself embeds the verified font as authorised for this project.
