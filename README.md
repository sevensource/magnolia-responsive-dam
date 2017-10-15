[![GitHub Tag](https://img.shields.io/github/tag/sevensource/magnolia-responsive-dam.svg?maxAge=3600)](https://github.com/sevensource/magnolia-responsive-dam/tags)
[![Maven Central](https://img.shields.io/maven-central/v/org.sevensource.magnolia/magnolia-responsive-dam.svg?maxAge=3600)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.sevensource.magnolia%22%20AND%20a%3A%22magnolia-responsive-dam%22)
[![License](https://img.shields.io/github/license/sevensource/magnolia-responsive-dam.svg)](https://github.com/sevensource/magnolia-responsive-dam/blob/master/LICENSE)


magnolia-responsive-dam
================================

Responsive images for [Magnolia](http://www.magnolia-cms.com) 5.5.x 

This module allows content editors to select multiple focus areas (i.e. cropping areas) of an image, which are specified by the means of aspect ratios (i.e. 16:9).

The module provides two fields, which are drop in replacements for their magnolia counterpart:
* AspectAwareDamLinkField (replacing LinkField for referencing images in DAM assets app)
* AspectAwareDamUploadField (replacing DamUploadField for storing images in website repository and also used by in the assets app itself). 


**Contributions welcome!**

Installation
=============


Configuration
=============
* change /modules/dam-app/apps/assets/subApps/detail/editor/form/tabs/asset/fields/resource
  class: org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition
  useExistingFocusAreas: true
  
* in /modules/responsive-dam/config/variations, add a variation set, for example:
  hero-area:
    mobile:
      aspect: "4:3"
    default:
      aspect: "16:9"
      
* add the field to a component:
	- name: heroimage
	  label: ImageUpload
	  variationSet: hero-area
	  class: org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition
	  binaryNodeName: image
	  editFileName: true

	or
    - name: heroimagelink
      label: ImageLink
      variationSet: hero-area
      class: org.sevensource.magnolia.responsivedam.field.link.AspectAwareDamLinkFieldDefinition
      targetWorkspace: dam
      appName: assets
      aspectsAppName: "dam-app:uploadAndEdit"
      identifierToPathConverter:
        class: info.magnolia.dam.app.assets.field.translator.AssetCompositeIdKeyTranslator
      contentPreviewDefinition:
        contentPreviewClass: info.magnolia.dam.app.ui.field.DamFilePreviewComponent
      

