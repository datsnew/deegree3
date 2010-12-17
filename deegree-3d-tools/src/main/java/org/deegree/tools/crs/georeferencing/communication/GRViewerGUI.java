//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tools.crs.georeferencing.communication;

import static org.deegree.tools.crs.georeferencing.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.deegree.rendering.r3d.opengl.display.OpenGLEventHandler;
import org.deegree.tools.crs.georeferencing.communication.checkboxlist.CheckboxListTransformation;
import org.deegree.tools.crs.georeferencing.communication.navigationbar.NavigationBarPanelFootprint;
import org.deegree.tools.crs.georeferencing.communication.navigationbar.NavigationBarPanelGeoref;
import org.deegree.tools.crs.georeferencing.communication.panel2D.BuildingFootprintPanel;
import org.deegree.tools.crs.georeferencing.communication.panel2D.Scene2DPanel;

/**
 * The <Code>GRViewerGUI</Code> class provides the client to view georeferencing tools/windows.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GRViewerGUI extends JFrame {

    private static final long serialVersionUID = -4984396613454027721L;

    private final static Dimension SUBCOMPONENT_DIMENSION = new Dimension( 1, 1 );

    private Scene2DPanel scenePanel2D;

    private JPanel panelWest;

    private JPanel panelEast;

    private OpenGLEventHandler openGLEventListener;

    private BuildingFootprintPanel footprintPanel;

    private JMenuItem editMenuItem;

    private JMenuItem openShape;

    private JMenuItem openWMS;

    private JMenuItem openBuilding;

    private NavigationBarPanelGeoref naviPanelGeoref;

    private NavigationBarPanelFootprint naviPanelFoot;

    private JMenu menuTransformation;

    private CheckboxListTransformation list;

    private JMenuItem exit;

    public GRViewerGUI() {
        super( get( "WINDOW_TITLE" ) );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbl.setConstraints( this, gbc );

        setLayout( gbl );
        setMinimumSize( GUIConstants.FRAME_DIMENSION );

        setPreferredSize( GUIConstants.FRAME_DIMENSION );

        setupMenubar();
        setup2DScene( gbl );
        setupPanelFootprint( gbl );
        setupOpenGL( gbl, false );
        this.pack();
    }

    private void setupMenubar() {

        JMenuBar menuBar;
        JMenu menuFile;
        JMenu menuEdit;

        menuBar = new JMenuBar();
        menuFile = new JMenu( get( "MENU_FILE" ) );
        menuEdit = new JMenu( get( "MENU_EDIT" ) );
        menuTransformation = new JMenu( get( "MENU_TRANSFORMATION" ) );

        menuBar.add( menuFile );
        menuBar.add( menuEdit );
        menuBar.add( menuTransformation );

        openShape = new JMenuItem( get( "MENUITEM_OPEN_SHAPEFILE" ) );
        openShape.setName( get( "MENUITEM_OPEN_SHAPEFILE" ) );
        openWMS = new JMenuItem( get( "MENUITEM_OPEN_WMS_LAYER" ) );
        openWMS.setName( get( "MENUITEM_OPEN_WMS_LAYER" ) );
        openBuilding = new JMenuItem( get( "MENUITEM_OPEN_BUILDING" ) );
        openBuilding.setName( get( "MENUITEM_OPEN_BUILDING" ) );
        exit = new JMenuItem( get( "MENUITEM_EXIT" ) );
        exit.setName( get( "MENUITEM_EXIT" ) );

        menuFile.add( openShape );
        menuFile.add( openWMS );
        menuFile.add( openBuilding );
        menuFile.add( exit );

        editMenuItem = new JMenuItem( get( "MENUITEM_EDIT_OPTIONS" ) );
        menuEdit.add( editMenuItem );

        this.getRootPane().setJMenuBar( menuBar );
    }

    private void setup2DScene( GridBagLayout gbl ) {
        panelWest = new JPanel( new BorderLayout() );
        scenePanel2D = new Scene2DPanel();
        naviPanelGeoref = new NavigationBarPanelGeoref();
        scenePanel2D.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        scenePanel2D.setPreferredSize( SUBCOMPONENT_DIMENSION );

        panelWest.add( naviPanelGeoref, BorderLayout.NORTH );
        panelWest.add( scenePanel2D, BorderLayout.CENTER );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, panelWest, 0, 1, 2, 2, 1.0, 1.0 );

    }

    private void setupPanelFootprint( GridBagLayout gbl ) {
        panelEast = new JPanel( new BorderLayout() );
        footprintPanel = new BuildingFootprintPanel();
        naviPanelFoot = new NavigationBarPanelFootprint();
        footprintPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        footprintPanel.setBackground( Color.white );
        footprintPanel.setPreferredSize( SUBCOMPONENT_DIMENSION );

        panelEast.add( naviPanelFoot, BorderLayout.NORTH );
        panelEast.add( footprintPanel, BorderLayout.CENTER );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, panelEast, 2, 1, 1, 1,
                                          footprintPanel.getInsets(), GridBagConstraints.LINE_END, .5, 1 );

    }

    private void setupOpenGL( GridBagLayout gbl, boolean testSphere ) {
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered( true );
        caps.setHardwareAccelerated( true );
        caps.setAlphaBits( 8 );
        caps.setAccumAlphaBits( 8 );
        openGLEventListener = new OpenGLEventHandler( testSphere );

        GLCanvas canvas = new GLCanvas( caps );
        canvas.addGLEventListener( openGLEventListener );
        canvas.addMouseListener( openGLEventListener.getTrackBall() );
        canvas.addMouseWheelListener( openGLEventListener.getTrackBall() );
        canvas.addMouseMotionListener( openGLEventListener.getTrackBall() );
        canvas.setPreferredSize( SUBCOMPONENT_DIMENSION );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, canvas, 2, 2, 1, 1, new Insets( 0, 10, 0, 0 ),
                                          GridBagConstraints.LINE_END, .5, 1 );
    }

    /**
     * not used at the moment
     */
    public void resetScene2D() {
        scenePanel2D.paint( new BufferedImage( 0, 0, BufferedImage.TYPE_3BYTE_BGR ).createGraphics() );
    }

    /**
     * Adds the actionListener to the visible components to interact with the user.
     * 
     * @param e
     */
    public void addListeners( ActionListener e ) {
        naviPanelGeoref.addCoordListener( e );
        naviPanelGeoref.addAbstractCoordListener( e );
        naviPanelFoot.addAbstractCoordListener( e );
        editMenuItem.addActionListener( e );
        openShape.addActionListener( e );
        openWMS.addActionListener( e );
        openBuilding.addActionListener( e );
        exit.addActionListener( e );
    }

    /**
     * The {@link Scene2DPanel} is a child of this Container
     * 
     */
    public Scene2DPanel getScenePanel2D() {
        return scenePanel2D;
    }

    public BuildingFootprintPanel getFootprintPanel() {
        return footprintPanel;
    }

    public void addHoleWindowListener( ComponentListener c ) {
        this.addComponentListener( c );

    }

    public NavigationBarPanelGeoref getNavigationPanelGeoref() {
        return naviPanelGeoref;
    }

    public NavigationBarPanelFootprint getNaviPanelFoot() {
        return naviPanelFoot;
    }

    public OpenGLEventHandler getOpenGLEventListener() {
        return openGLEventListener;
    }

    /**
     * Sets everything that is needed to handle userinteraction with the checkboxes in the transformationMenu.
     * 
     * @param selectedCheckbox
     *            the checkbox that has been selected by the user.
     */
    public void activateTransformationCheckbox( JCheckBox selectedCheckbox ) {
        this.list.getModel().selectThisCheckbox( selectedCheckbox );
        this.menuTransformation.getPopupMenu().setVisible( false );
        this.menuTransformation.setVisible( true );
        this.menuTransformation.setSelected( false );
    }

    public JMenu getMenuTransformation() {
        return menuTransformation;
    }

    public void addToMenuTransformation( CheckboxListTransformation list ) {
        this.list = list;
        for ( JCheckBox box : list.getModel().getList() ) {
            this.menuTransformation.add( box );
        }

    }

}
