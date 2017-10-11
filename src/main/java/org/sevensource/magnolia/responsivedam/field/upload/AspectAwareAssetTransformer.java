package org.sevensource.magnolia.responsivedam.field.upload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.sevensource.magnolia.responsivedam.field.FocusArea;
import org.sevensource.magnolia.responsivedam.field.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

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

	private static final String JCR_CONTENT = "jcr:content";

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareAssetTransformer.class);

	public static final String PROP_ASPECTS = "focusAreas";
	private static final String PROP_X = "x";
	private static final String PROP_Y = "y";
	private static final String PROP_WIDTH = "width";
	private static final String PROP_HEIGHT = "height";
	
	
    @Inject
    public AspectAwareAssetTransformer(Item relatedFormItem, DamUploadFieldDefinition definition, I18NAuthoringSupport i18nAuthoringSupport) {
        super(relatedFormItem, definition, AspectAwareAssetUploadReceiver.class, i18nAuthoringSupport);
    }
    
    @Override
    protected AspectAwareAssetUploadReceiver initializeUploadReceiver() {
        return Components.newInstance(AspectAwareAssetUploadReceiver.class);
    }
    
    
    @Override
    public Item populateItem(AspectAwareAssetUploadReceiver newValue, Item item) {
    	
        super.populateItem(newValue, item);

        final Item rootItem = getRootItem();


        final Item focusAreasItem = getOrCreateFocusAreasItem((AbstractJcrNodeAdapter) rootItem); 
        ((AbstractJcrNodeAdapter)rootItem).addChild((AbstractJcrNodeAdapter) focusAreasItem);
        
        final Set<String> focusAreaKeys;
        
        if(newValue != null && newValue.getFocusAreas() != null && newValue.getFocusAreas().getAreas() != null) {
        	focusAreaKeys = newValue.getFocusAreas().getAreas().keySet();
        	
        	for(Entry<String, FocusArea> entry : newValue.getFocusAreas().getAreas().entrySet()) {
            	Item focusAreaItem = getOrCreateItem((AbstractJcrNodeAdapter) focusAreasItem, entry.getKey());

            	FocusArea area = entry.getValue();
            	
                getOrCreateProperty(focusAreaItem, PROP_X, Long.class).setValue((long) area.getX());
                getOrCreateProperty(focusAreaItem, PROP_Y, Long.class).setValue((long) area.getY());
                getOrCreateProperty(focusAreaItem, PROP_WIDTH, Long.class).setValue((long) area.getWidth());
                getOrCreateProperty(focusAreaItem, PROP_HEIGHT, Long.class).setValue((long) area.getHeight());
                
                ((JcrNodeAdapter)focusAreasItem).addChild((JcrNodeAdapter)focusAreaItem);
        	}
        } else {
        	focusAreaKeys = Collections.emptySet();
        }
        
        if(focusAreaKeys.isEmpty()) {
        	((JcrNodeAdapter)rootItem).removeChild((AbstractJcrNodeAdapter) focusAreasItem);
        } else {
        	// remove stale entries
        	List<AbstractJcrNodeAdapter> childrenToBeRemoved = new ArrayList<>();
        	
        	for(Entry<String, AbstractJcrNodeAdapter> entry : ((JcrNodeAdapter)focusAreasItem).getChildren().entrySet()) {
        		if(! focusAreaKeys.contains(entry.getKey())) {
        			childrenToBeRemoved.add((JcrNodeAdapter) entry.getValue());
        		}
        	}
        	
        	for(AbstractJcrNodeAdapter r : childrenToBeRemoved) {
        		((JcrNodeAdapter)focusAreasItem).removeChild(r);
        	}
        }

        return item;
    }
    
    @Override
    public AspectAwareAssetUploadReceiver createPropertyFromItem(Item item) {
    	if(! (item instanceof AbstractJcrNodeAdapter)) {
    		throw new IllegalArgumentException("item is of type " + item.getClass().getName());
    	}
    	
    	AspectAwareAssetUploadReceiver uploadReceiver = super.createPropertyFromItem(item);
    	
    	if(! uploadReceiver.isEmpty() && uploadReceiver.isImage()) {
    		final Item rootItem = getRootItem();
    		
    		FocusAreas focusAreas = new FocusAreas();
    		
    		Item aspectsItem = getOrCreateFocusAreasItem((AbstractJcrNodeAdapter) rootItem);
    		
    		Map<String, AbstractJcrNodeAdapter> aspectsChildren = ((AbstractJcrNodeAdapter)aspectsItem).getChildren();
        	
        	for(Entry<String, AbstractJcrNodeAdapter> childEntry : aspectsChildren.entrySet()) {
        		final String name = childEntry.getKey();
        		final Item childItem = getOrCreateItem((JcrNodeAdapter) aspectsItem, name);
        		
        		FocusArea focusArea = getOrCreateFocusAreaFromItem(childItem);
        		if(focusArea == null) {
        			logger.error("Invalid FocusArea for {}", name);
        		} else {
        			focusAreas.addArea(name, focusArea);
        		}
        	}
        	
        	uploadReceiver.setFocusArea(focusAreas);
    	}
    	
    	return uploadReceiver;
    }
    
    @Override
    protected JcrNodeAdapter getRootItem() {
    	final String itemName = getItemName();
    	if(itemName.equals(JCR_CONTENT)) {
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
    	return getOrCreateItem(getRootItem(), JCR_CONTENT);
    }
    
    private Item getOrCreateFocusAreasItem(AbstractJcrNodeAdapter item) {
        return getOrCreateItem(item, PROP_ASPECTS);
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
    
    private FocusArea getOrCreateFocusAreaFromItem(Item item) {
    	Property<Long> pX = getOrCreateProperty(item, PROP_X, Long.class);
    	Property<Long> pY = getOrCreateProperty(item, PROP_Y, Long.class);
    	Property<Long> pW = getOrCreateProperty(item, PROP_WIDTH, Long.class);
    	Property<Long> pH = getOrCreateProperty(item, PROP_HEIGHT, Long.class);

    	final Integer x = longToInteger(pX.getValue());
    	final Integer y = longToInteger(pY.getValue());
    	final Integer w = longToInteger(pW.getValue());        		
    	final Integer h = longToInteger(pH.getValue());
    	
    	FocusArea focusArea = new FocusArea(x, y, w, h);
    	
    	return focusArea.isValid() ? focusArea : null;
    }
}
