package com.kmpproject.uikit.theme

val LightAppColors = AppColors(
  isLight = true,
  accent = AppColors.Accent(
    brand = ColorPalette.purple50,
    destructive = ColorPalette.red50
  ),
  background = AppColors.Background(
    brand = ColorPalette.purple5,
    primary = ColorPalette.white,
    tertiary = ColorPalette.platinumGray4
  ),
  button = AppColors.Button(
    primaryBackground = ColorPalette.purple50,
    primaryBackgroundPressed = ColorPalette.purple60,
    primaryForeground = ColorPalette.white,
    secondaryForeground = ColorPalette.platinumGray90
  ),
  divider = AppColors.Divider(
    primary = ColorPalette.platinumGray7
  ),
  dragger = ColorPalette.blackAlpha10,
  icon = AppColors.Icon(
    invert = ColorPalette.white,
    primary = ColorPalette.platinumGray90,
    secondary = ColorPalette.platinumGray60
  ),
  pins = AppColors.Pins(
    foreground = ColorPalette.white,
    primaryBackground = ColorPalette.platinumGray80,
    stroke = ColorPalette.white
  ),
  skeleton = AppColors.Skeleton(
    background = ColorPalette.blackAlpha8,
    highlight = ColorPalette.blackAlpha8
  ),
  text = AppColors.Text(
    primary = ColorPalette.platinumGray90,
    secondary = ColorPalette.platinumGray60,
    tertiary = ColorPalette.platinumGray40
  )
)

val DarkAppColors = AppColors(
  isLight = false,
  accent = AppColors.Accent(
    brand = ColorPalette.purple60,
    destructive = ColorPalette.red50
  ),
  background = AppColors.Background(
    brand = ColorPalette.purple60,
    primary = ColorPalette.gray90,
    tertiary = ColorPalette.gray80
  ),
  button = AppColors.Button(
    primaryBackground = ColorPalette.purple60,
    primaryBackgroundPressed = ColorPalette.purple60,
    primaryForeground = ColorPalette.white,
    secondaryForeground = ColorPalette.whiteAlpha70
  ),
  divider = AppColors.Divider(
    primary = ColorPalette.gray80
  ),
  dragger = ColorPalette.whiteAlpha50,
  icon = AppColors.Icon(
    invert = ColorPalette.gray90,
    primary = ColorPalette.white,
    secondary = ColorPalette.gray20
  ),
  pins = AppColors.Pins(
    foreground = ColorPalette.white,
    primaryBackground = ColorPalette.platinumGray80,
    stroke = ColorPalette.white
  ),
  skeleton = AppColors.Skeleton(
    background = ColorPalette.whiteAlpha8,
    highlight = ColorPalette.whiteAlpha8
  ),
  text = AppColors.Text(
    primary = ColorPalette.white,
    secondary = ColorPalette.gray20,
    tertiary = ColorPalette.gray40
  )
)
