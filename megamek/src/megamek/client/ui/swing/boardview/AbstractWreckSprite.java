/*
 * MegaMek - Copyright (C) 2020 - The MegaMek Team
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.client.ui.swing.boardview;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import megamek.client.ui.swing.GUIPreferences;
import megamek.client.ui.swing.util.EntityWreckHelper;
import megamek.common.Entity;
import megamek.common.Terrains;
import megamek.common.util.ImageUtil;

/**
 * Contains common functionality for wreck sprites (currently isometric and regular)
 * @author NickAragua
 *
 */
public abstract class AbstractWreckSprite extends Sprite {
    protected Entity entity;

    protected Rectangle modelRect;

    protected int secondaryPos;
    
    public AbstractWreckSprite(BoardView1 boardView1) {
        super(boardView1);
    }
    
    @Override
    public Rectangle getBounds() {
        Rectangle tempBounds = new Rectangle(bv.hex_size).union(modelRect);
        tempBounds.setLocation(bv.getHexLocation(entity.getPosition()));
        bounds = tempBounds;

        return bounds;
    }

    /**
     * Creates the sprite for this entity. It is an extra pain to create
     * transparent images in AWT.
     */
    @Override
    public void prepare() {
        // figure out size
        String shortName = entity.getShortName();
        Font font = new Font("SansSerif", Font.PLAIN, 10); //$NON-NLS-1$
        Rectangle tempRect = new Rectangle(47, 55, bv.getFontMetrics(font)
                .stringWidth(shortName) + 1, bv.getFontMetrics(font)
                .getAscent());

        // create image for buffer
        image = ImageUtil.createAcceleratedImage(bounds.width, bounds.height);
        Graphics2D graph = (Graphics2D) image.getGraphics();
        
        // if the entity is underwater or would sink underwater
        boolean entityIsUnderwater = (entity.relHeight() < 0) ||
                ((entity.relHeight() == 0) && entity.getGame().getBoard().getHex(entity.getPosition()).containsTerrain(Terrains.WATER));
        
        if(entityIsUnderwater) {
            graph.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.35f));
        }

        // draw the 'destroyed decal' where appropriate
        boolean useBottomLayer = EntityWreckHelper.displayDestroyedDecal(entity);
        
        if(useBottomLayer) {
            Image destroyed = bv.tileManager.bottomLayerWreckMarkerFor(entity, 0);
            if (null != destroyed) {
                graph.drawImage(destroyed, 0, 0, this);
            }
        }
        
        // draw the 'fuel leak' decal where appropriate
        boolean drawFuelLeak = EntityWreckHelper.displayFuelLeak(entity);
        
        if(drawFuelLeak) {
            Image fuelLeak = bv.tileManager.bottomLayerFuelLeakMarkerFor(entity);
            if (null != fuelLeak) {
                graph.drawImage(fuelLeak, 0, 0, this);
            }
        }
        
        // draw the 'tires' or 'tracks' decal where appropriate
        Image motiveWreckage = bv.tileManager.bottomLayerMotiveMarkerFor(entity);
        if (null != motiveWreckage) {
            graph.drawImage(motiveWreckage, 0, 0, this);
        }
        
        // Draw wreck image, if we've got one.
        Image wreck = null;
        
        if(EntityWreckHelper.displayDevastation(entity)) {
            wreck = bv.tileManager.getCraterFor(entity, secondaryPos);
        } else {
            wreck = useBottomLayer ? 
                    bv.tileManager.imageFor(entity, secondaryPos) :
                    bv.tileManager.wreckMarkerFor(entity, secondaryPos);
        }

        if (null != wreck) {
            graph.drawImage(wreck, 0, 0, this);
        }
        
        if(entityIsUnderwater) {
            graph.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0f));
        }
        
        if ((secondaryPos == -1) && GUIPreferences.getInstance()
                .getBoolean(GUIPreferences.ADVANCED_DRAW_ENTITY_LABEL)) {
            // draw box with shortName
            Color text = Color.lightGray;
            Color bkgd = Color.darkGray;
            Color bord = Color.black;

            graph.setFont(font);
            graph.setColor(bord);
            graph.fillRect(tempRect.x, tempRect.y, tempRect.width,
                    tempRect.height);
            tempRect.translate(-1, -1);
            graph.setColor(bkgd);
            graph.fillRect(tempRect.x, tempRect.y, tempRect.width,
                    tempRect.height);
            graph.setColor(text);
            graph.drawString(shortName, tempRect.x + 1,
                    (tempRect.y + tempRect.height) - 1);
        }

        // create final image
        image = bv.getScaledImage(image, false);
        graph.dispose();
    }

    /**
     * Overrides to provide for a smaller sensitive area.
     */
    @Override
    public boolean isInside(Point point) {
        return false;
    }
}
