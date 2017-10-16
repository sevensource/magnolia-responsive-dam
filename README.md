[![GitHub Tag](https://img.shields.io/github/tag/sevensource/magnolia-responsive-dam.svg?maxAge=3600)](https://github.com/sevensource/magnolia-responsive-dam/tags)
[![Maven Central](https://img.shields.io/maven-central/v/org.sevensource.magnolia/magnolia-responsive-dam.svg?maxAge=3600)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.sevensource.magnolia%22%20AND%20a%3A%22magnolia-responsive-dam%22)
[![License](https://img.shields.io/github/license/sevensource/magnolia-responsive-dam.svg)](https://github.com/sevensource/magnolia-responsive-dam/blob/master/LICENSE)
# magnolia-responsive-dam
Responsive images for [Magnolia](http://www.magnolia-cms.com) 5.5.x 
================================


This module provides:

1. Two fields which allow content editors to specify multiple focus areas (i.e. cropping areas) of an image. The definable focus areas are specified by the means of aspect ratios (i.e. 16:9):
    - AspectAwareDamLinkField (replacing LinkField for referencing images in DAM assets app)
    - AspectAwareDamUploadField (replacing DamUploadField for storing images in website repository and also used by in the assets app itself).

2. TemplatingFunctions to assist in generating the responsive HTML markup (i.e. srcset, etc.)

3. An ImageOperationChain, which integrates into Magnolias imaging module to generate the image variations


**Contributions welcome!**

Installation
=============
The module is available on Maven central
```xml
<dependency>
  <groupId>org.sevensource.magnolia</groupId>
  <artifactId>magnolia-responsive-dam</artifactId>
  <version>x.x.x</version>
</dependency>
```

Upon module installation, 
* a contextAttribute (_responsivedamfn_) is installed into `/modules/rendering/renderers/freemarker/contextAttributes`
* the ImageOperationChain is installed into `/modules/imaging/config/generators/rd`
* a default config is installed into `/modules/responsive-dam/config`

Configuration
=============
* change `/modules/dam-app/apps/assets/subApps/detail/editor/form/tabs/asset/fields/resource` and add the following properties:
```
  class: org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition
  useExistingFocusAreas: true
```
* in `/modules/responsive-dam/config/variations`, add a variation set, for example:
```
  hero-area:
    mobile:
      aspect: "4:3"
      constraints.minimumSize: 320w
      constraints.maximumSize: 576w
    default:
      aspect: "16:9"
      constraints.minimumSize: 576w
      constraints.maximumSize: 1600w
```   
* add a responsive-dam field to your component:
  * either an *AspectAwareDamUploadField* (equivalent to Magnolias *DamUploadField*)
    ```
    - name: heroimage
      label: ImageUpload
      binaryNodeName: heroimage
      variationSet: hero-area
      class: org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition
      editFileName: true
    ```
  * or an *AspectAwareDamLinkField* (equivalent to Magnolias *LinkField*) for storing the image in DAM
    ```
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
    ```

Usage
=====
After uploading an image, you will find an additional button to specify the required focus areas.
In your template, you can use the provided [ResponsiveDamTemplatingFunctions](/src/main/java/org/sevensource/magnolia/responsivedam/templating/) for rendering support:

```html
[#assign imageNode = cmsfn.asJCRNode(content).getNode('heroimage') /]
<#-- use damfn.getAsset(content.imagelink).getNode() if the image is in DAM -->

[#assign variationMobile = responsivedamfn.getResponsiveVariation(imageNode, 'hero-area', 'mobile') /]
[#assign variationDefault = responsivedamfn.getResponsiveVariation(imageNode, 'hero-area', 'default') /]

[#assign mobileSrcSet = responsivedamfn.generateSrcSet(variationMobile.getRenditions()) /]
[#assign defaultSrcSet = responsivedamfn.generateSrcSet(variationDefault.getRenditions()) /]

<picture>
  <source media="(max-width: 575px)" srcset="${mobileSrcSet}">
  <img src="${variationDefault.getDefaultRendition().getLink()}" srcset="${defaultSrcSet}">
</picture>
```


Advanced Configuration
======================
* Image format mappings and their parameters are specified `/modules/responsive-dam/config/outputFormatMappings`. Each sourceMimeType can have multiple outputFormats (useful for example for webp support)
* Next to the minimum and maximum size for each variation, you can specify the maximum number of renditions, that are generated and the minimum pixel difference between each rendition:
  ```
      default:
        aspect: "2:1"
        constraints.minimumSize: 800w
        constraints.maximumSize: 1600w
        constraints.maximumResolutions: 3
        constraints.minimumResolutionSizeStep: 200
  ```
  The defaults are specified in `/modules/responsive-dam/config/defaultConstraint`.
