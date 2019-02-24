package org.sevensource.magnolia.responsivedam.field.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jackrabbit.JcrConstants;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreaSet;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

import info.magnolia.dam.app.ui.field.definition.DamUploadFieldDefinition;
import info.magnolia.dam.app.ui.field.transformer.AssetTransformer;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.vaadin.integration.jcr.AbstractJcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

public class AspectAwareAssetTransformer extends AssetTransformer<AspectAwareAssetUploadReceiver> {

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareAssetTransformer.class);

	public  static final String PROP_FOCUSAREAS = "focusAreas";
	private static final String PROP_X = "x";
	private static final String PROP_Y = "y";
	private static final String PROP_WIDTH = "width";
	private static final String PROP_HEIGHT = "height";

	@Inject
	public AspectAwareAssetTransformer(Item relatedFormItem, DamUploadFieldDefinition definition,
			I18NAuthoringSupport i18nAuthoringSupport) {
		super(relatedFormItem, definition, AspectAwareAssetUploadReceiver.class, i18nAuthoringSupport);
	}

	@Override
	protected AspectAwareAssetUploadReceiver initializeUploadReceiver() {
		return Components.newInstance(AspectAwareAssetUploadReceiver.class);
	}

	private void removeStaleItems(AspectAwareAssetUploadReceiver newValue) {

		final Item rootItem = getRootItem();
		final Item focusAreasItem = getOrCreateFocusAreasItem((AbstractJcrNodeAdapter) rootItem);

		if(newValue == null || newValue.getFocusAreas() == null) {
			((JcrNodeAdapter) rootItem).removeChild((AbstractJcrNodeAdapter) focusAreasItem);
		} else {
			final FocusAreas focusAreas = newValue.getFocusAreas();

			final List<AbstractJcrNodeAdapter> focusAreaSetsToBeRemoved = new ArrayList<>();

			for (Entry<String, AbstractJcrNodeAdapter> focusAreaSetEntry : ((JcrNodeAdapter) focusAreasItem).getChildren().entrySet()) {
				final String focusAreaSetName = focusAreaSetEntry.getKey();

				if(focusAreas.getFocusAreaSet(focusAreaSetName) == null) {
					focusAreaSetsToBeRemoved.add(focusAreaSetEntry.getValue());
				} else {
					final Item focusAreaSetItem = getOrCreateItem((AbstractJcrNodeAdapter) focusAreasItem, focusAreaSetName);

					final List<AbstractJcrNodeAdapter> focusAreasToBeRemoved = new ArrayList<>();

					for (Entry<String, AbstractJcrNodeAdapter> focusAreaEntry : ((JcrNodeAdapter) focusAreaSetItem).getChildren().entrySet()) {
						final String focusAreaName = focusAreaEntry.getKey();

						if(focusAreas.getFocusAreaSet(focusAreaSetName).getFocusArea(focusAreaName) == null) {
							focusAreasToBeRemoved.add(focusAreaEntry.getValue());
						}
					}

					for (AbstractJcrNodeAdapter r : focusAreasToBeRemoved) {
						((JcrNodeAdapter) focusAreaSetItem).removeChild(r);
					}
				}
			}

			for (AbstractJcrNodeAdapter r : focusAreaSetsToBeRemoved) {
				((JcrNodeAdapter) focusAreasItem).removeChild(r);
			}
		}
	}

	@Override
	public Item populateItem(AspectAwareAssetUploadReceiver newValue, Item item) {

		super.populateItem(newValue, item);
		final Item rootItem = getRootItem();

		final Item focusAreasItem = getOrCreateFocusAreasItem((AbstractJcrNodeAdapter) rootItem);
		((AbstractJcrNodeAdapter) rootItem).addChild((AbstractJcrNodeAdapter) focusAreasItem);

		removeStaleItems(newValue);


		if (newValue != null &&
				newValue.getFocusAreas() != null &&
				! CollectionUtils.isEmpty(newValue.getFocusAreas().getFocusAreaSets()) &&
				! CollectionUtils.isEmpty(newValue.getFocusAreas().getFocusAreaSets().get(0).getFocusAreas())) {


			final FocusAreas focusAreas = newValue.getFocusAreas();


			for(FocusAreaSet focusAreaSet : focusAreas.getFocusAreaSets()) {
				final Item focusAreaSetItem = getOrCreateItem((AbstractJcrNodeAdapter) focusAreasItem, focusAreaSet.getName());
				((JcrNodeAdapter) focusAreasItem).addChild((JcrNodeAdapter) focusAreaSetItem);

				for (FocusArea focusArea : focusAreaSet.getFocusAreas()) {
					final Item focusAreaItem = getOrCreateItem((AbstractJcrNodeAdapter) focusAreaSetItem, focusArea.getName());

					getOrCreateProperty(focusAreaItem, PROP_X, Long.class).setValue((long) focusArea.getX());
					getOrCreateProperty(focusAreaItem, PROP_Y, Long.class).setValue((long) focusArea.getY());
					getOrCreateProperty(focusAreaItem, PROP_WIDTH, Long.class).setValue((long) focusArea.getWidth());
					getOrCreateProperty(focusAreaItem, PROP_HEIGHT, Long.class).setValue((long) focusArea.getHeight());

					((JcrNodeAdapter) focusAreaSetItem).addChild((JcrNodeAdapter) focusAreaItem);
				}
			}
		}

		return item;
	}

	@Override
	public AspectAwareAssetUploadReceiver createPropertyFromItem(Item item) {
		if (!(item instanceof AbstractJcrNodeAdapter)) {
			throw new IllegalArgumentException("item is of type " + item.getClass().getName());
		}

		AspectAwareAssetUploadReceiver uploadReceiver = super.createPropertyFromItem(item);

		if (!uploadReceiver.isEmpty() && uploadReceiver.isImage()) {
			final Item rootItem = getRootItem();

			final FocusAreas focusAreas = new FocusAreas();

			final Item focusAreasItem = getOrCreateFocusAreasItem((AbstractJcrNodeAdapter) rootItem);

			final Map<String, AbstractJcrNodeAdapter> focusAreaSets = ((AbstractJcrNodeAdapter) focusAreasItem).getChildren();

			for (Entry<String, AbstractJcrNodeAdapter> focusAreaSetEntry : focusAreaSets.entrySet()) {
				final String focusAreaSetName = focusAreaSetEntry.getKey();
				final Item focusAreaSetItem = getOrCreateItem((JcrNodeAdapter) focusAreasItem, focusAreaSetName);

				final Map<String, AbstractJcrNodeAdapter> focusAreaSetChildren = ((AbstractJcrNodeAdapter) focusAreaSetItem).getChildren();

				final FocusAreaSet focusAreaSet = new FocusAreaSet();
				focusAreaSet.setName(focusAreaSetName);
				focusAreas.addFocusAreaSet(focusAreaSet);

				for (Entry<String, AbstractJcrNodeAdapter> childEntry : focusAreaSetChildren.entrySet()) {
					final String focusAreaName = childEntry.getKey();
					final Item focusAreaItem = getOrCreateItem((JcrNodeAdapter) focusAreaSetItem, focusAreaName);

					final FocusArea focusArea = getOrCreateFocusAreaFromItem(focusAreaName, focusAreaItem);
					if (focusArea == null) {
						logger.error("Invalid FocusArea for {}/{}", focusAreaSetName, focusAreaName);
					} else {
						focusAreaSet.addFocusArea(focusArea);
					}
				}
			}

			uploadReceiver.setFocusArea(focusAreas);
		}

		return uploadReceiver;
	}

	@Override
	protected JcrNodeAdapter getRootItem() {
		final String itemName = getItemName();
		if (itemName.equals(JcrConstants.JCR_CONTENT)) {
			return super.getRootItem();
		} else {
			JcrNodeAdapter superRootItem = super.getRootItem();
			JcrNodeAdapter myRootItem = (JcrNodeAdapter) getOrCreateItem(superRootItem, itemName);
			superRootItem.addChild(myRootItem);
			return myRootItem;
		}
	}

	@Override
	protected Item getOrCreateFileItem() {
		return getOrCreateItem(getRootItem(), JcrConstants.JCR_CONTENT);
	}

	private Item getOrCreateFocusAreasItem(AbstractJcrNodeAdapter item) {
		return getOrCreateItem(item, PROP_FOCUSAREAS);
	}

	private Item getOrCreateItem(AbstractJcrNodeAdapter item, String itemName) {
		AbstractJcrNodeAdapter child = item.getChild(itemName);
		if (child != null) {
			if (!child.hasChildItemChanges()) {
				populateStoredChildItems((JcrNodeAdapter) child);
			}
			return child;
		}

		Node node = null;
		try {
			node = item.getJcrItem();
			if (node.hasNode(itemName) && !(item instanceof JcrNewNodeAdapter)) {
				child = new JcrNodeAdapter(node.getNode(itemName));
			} else {
				child = new JcrNewNodeAdapter(node, NodeTypes.ContentNode.NAME, itemName);
			}
		} catch (RepositoryException e) {
			logger.error("Could get or create a child Item for {} ", NodeUtil.getPathIfPossible(node), e);
			throw new RuntimeException(e);
		}

		return child;
	}

	private static Integer longToInteger(Long value) {
		return value != null ? value.intValue() : null;
	}

	private FocusArea getOrCreateFocusAreaFromItem(String focusAreaName, Item item) {
		Property<Long> pX = getOrCreateProperty(item, PROP_X, Long.class);
		Property<Long> pY = getOrCreateProperty(item, PROP_Y, Long.class);
		Property<Long> pW = getOrCreateProperty(item, PROP_WIDTH, Long.class);
		Property<Long> pH = getOrCreateProperty(item, PROP_HEIGHT, Long.class);

		final Integer x = longToInteger(pX.getValue());
		final Integer y = longToInteger(pY.getValue());
		final Integer w = longToInteger(pW.getValue());
		final Integer h = longToInteger(pH.getValue());

		final FocusArea focusArea = new FocusArea(focusAreaName, x, y, w, h);
		
		if(logger.isDebugEnabled() && ! focusArea.isValid()) {
			logger.warn("FocusArea read is not valid: {}", focusArea);
		}
		
		return focusArea.isValid() ? focusArea : null;
	}
}
