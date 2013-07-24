package vvide.ui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import vvide.Application;
import vvide.MarkerManager;
import vvide.signal.Marker;
import vvide.signal.TimeMetric;
import vvide.ui.AbstractView;
import vvide.ui.views.marker.SortedListModel;
import vvide.utils.CommonMethods;
import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A View to manage the Markers
 */
public class MarkerView extends AbstractView {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * ID for a MarkerView
	 */
	public static int MARKER_VIEW_ID = 3;
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -3642154164707940706L;
	/**
	 * List with Markers
	 */
	private JList listMarkers;
	/**
	 * TextBox with position
	 */
	private JTextField tbxPosition;
	/**
	 * TextBox with distance to the selected Marker
	 */
	private JTextField tbxDistanceTo;
	/**
	 * TextBox with name of the Marker
	 */
	private JTextField tbxName;
	/**
	 * ComboBox for the selecting the marker to calculate the distance
	 */
	private JComboBox cmbxMarkers;
	/**
	 * Button to save changes
	 */
	private JButton btnSaveChanges;
	/**
	 * Model for a Marker list
	 */
	private SortedListModel listModel;
	/**
	 * Model for a Combobox
	 */
	private DefaultComboBoxModel comboboxModel;
	/**
	 * MarkerManager Listener
	 */
	private MarkersClearedListener markersClearedListener =
			new MarkersClearedListener();
	/**
	 * Listener for an adding a marker
	 */
	private MarkerAddedListener markerAddedListener = new MarkerAddedListener();
	/**
	 * Listener for a removing a marker
	 */
	private MarkerRemovedListener markerRemovedListener =
			new MarkerRemovedListener();
	/**
	 * Listener for a changing a marker
	 */
	private MarkerChangedListener markerChangedListener =
			new MarkerChangedListener();
	/**
	 * Listener for a selecting a marker
	 */
	private MarkerSelectedListener markerSelectdListener =
			new MarkerSelectedListener();

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public boolean isStatic() {
		return false;
	}

	/*
	 * ============================ Methods ==================================
	 */
	public MarkerView( int id ) {
		super( "Markers", null, id );

		// Make UI
		JPanel content =
				new JPanel( new MigLayout( "", "[100.00,grow]", "[grow][]" ) );

		listMarkers = new JList();
		JScrollPane scrollPane = new JScrollPane( listMarkers );
		content.add( scrollPane, "cell 0 0,grow" );

		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder( null, "Selected Marker:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		panel.setLayout( new MigLayout( "", "[100.00][grow]", "[][][][][]" ) );

		JLabel lblName = new JLabel( "Name:" );
		panel.add( lblName, "cell 0 0" );

		tbxName = new JTextField();
		panel.add( tbxName, "cell 1 0,growx" );

		JLabel lblPosition = new JLabel( "Position:" );
		panel.add( lblPosition, "cell 0 1" );

		tbxPosition = new JTextField();
		panel.add( tbxPosition, "cell 1 1,growx" );

		JLabel lblDistanceTo = new JLabel( "Distance to:" );
		panel.add( lblDistanceTo, "cell 0 2" );

		cmbxMarkers = new JComboBox();
		panel.add( cmbxMarkers, "cell 1 2,growx" );

		tbxDistanceTo = new JTextField();
		panel.add( tbxDistanceTo, "cell 1 3,growx" );

		btnSaveChanges = new JButton( "Save" );
		panel.add( btnSaveChanges, "cell 1 4,alignx right" );

		content.add( panel, "cell 0 1,grow" );

		setComponent( content );

		// Creating models
		resetModels();
		

		// Adding listeners to the selected signal
		listMarkers.addListSelectionListener( new ListSelectionListener() {

			@Override
			public void valueChanged( ListSelectionEvent e ) {
				Marker selected = (Marker) listMarkers.getSelectedValue();
				Application.markerManager.setSelectedMarker( selected );
			}
		} );

		// Listener to scroll to the signal
		listMarkers.addMouseListener( new MouseAdapter() {

			@Override
			public void mouseClicked( MouseEvent e ) {
				if ( e.getClickCount() < 2 ) return;
				Application.actionManager.getAction( "ScrollToMarkerAction" )
						.actionPerformed( null );
			}

			@Override
			public void mousePressed( MouseEvent e ) {
				CommonMethods.maybeShowPopup( e, popupMenu, listMarkers );
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				CommonMethods.maybeShowPopup( e, popupMenu, listMarkers );
			}

		} );

		// Adding listener to save changes
		btnSaveChanges.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				Marker selectedMarker =
						Application.markerManager.getSelectedMarker();
				if ( selectedMarker != null ) {
					long position =
							TimeMetric.fromString( tbxPosition.getText(),
									Application.signalManager.getScaleUnit() );
					Application.markerManager.changeMarker( selectedMarker,
							tbxName.getText(), position );
				}
			}
		} );
	}
	
	@Override
	public void initView() {
		super.initView();		
		// Load popup menu
		popupMenu = CommonMethods.getPopUpMenu( "marker_view" );

		resetModels();
		loadMarkers();
		
		// Adding a listeners to the MarkerManager
		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKERS_CLEARED, markersClearedListener );
		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_ADDED, markerAddedListener );
		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_REMOVED, markerRemovedListener );
		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_CHANGED, markerChangedListener );
		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_SELECTED, markerSelectdListener );
	}
	
	/**
	 * Show the information about the selected marker
	 * 
	 * @param marker
	 *        a marker to show information
	 */
	private void showMarkerInfo( Marker marker ) {
		if ( marker == null ) {
			clearMarkerInfo();
			return;
		}
		tbxName.setText( marker.getName() );
		tbxPosition.setText( TimeMetric.toString( marker.getPosition(),
				Application.signalManager.getScaleUnit() ) );
		calcDistance();
	}

	/**
	 * Calc a distance between the selected marker in the list and combobox
	 */
	private void calcDistance() {
		Marker toMarker = (Marker) cmbxMarkers.getSelectedItem();
		if ( (Application.markerManager.getSelectedMarker() != null)
			&& (toMarker != null) ) {
			tbxDistanceTo.setText( TimeMetric.toString( Math
					.abs( Application.markerManager.getSelectedMarker()
							.getPosition()
						- toMarker.getPosition() ), Application.signalManager
					.getScaleUnit() ) );
		}
		else {
			tbxDistanceTo.setText( "" );
		}
	}

	/**
	 * Clear the Marker Information Area
	 */
	public void clearMarkerInfo() {
		tbxName.setText( "" );
		tbxPosition.setText( "" );
		tbxDistanceTo.setText( "" );
	}

//	Application.markerManager.removePropertyChangeListener(
//			MarkerManager.MARKERS_CLEARED, markersClearedListener );
//	Application.markerManager.removePropertyChangeListener(
//			MarkerManager.MARKER_ADDED, markerAddedListener );
//	Application.markerManager.removePropertyChangeListener(
//			MarkerManager.MARKER_REMOVED, markerRemovedListener );
//	Application.markerManager.removePropertyChangeListener(
//			MarkerManager.MARKER_CHANGED, markerChangedListener );
//	Application.markerManager.removePropertyChangeListener(
//			MarkerManager.MARKER_SELECTED, markerSelectdListener );

	
	/**
	 * Create and set new models
	 */
	private void resetModels() {
		listModel = new SortedListModel();
		comboboxModel = new DefaultComboBoxModel();
		cmbxMarkers.setModel( comboboxModel );
		listMarkers.setModel( listModel );
	}

	/**
	 * Load markers from the marker manager
	 */
	public void loadMarkers() {
		for ( Marker marker : Application.markerManager.getMarkers() ) {
			listModel.add( marker );
			comboboxModel.addElement( marker );
		}
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Listener for MarkerManager
	 */
	private class MarkersClearedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			clearMarkerInfo();
			resetModels();
		}
	}

	/**
	 * Listener for Adding a Marker
	 */
	private class MarkerAddedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// Add marker to the list and combobox
			listModel.add( evt.getNewValue() );
			comboboxModel.addElement( evt.getNewValue() );
		}

	}

	/**
	 * Listener for Removing a Marker
	 */
	private class MarkerRemovedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// Remove marker from the list and combobox
			listModel.removeElement( evt.getOldValue() );
			comboboxModel.removeElement( evt.getOldValue() );
		}

	}

	/**
	 * Listener for Changing a Marker
	 */
	private class MarkerChangedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// notify model about changes
			listModel.updateElement( evt.getNewValue() );
			int index = comboboxModel.getIndexOf( evt.getNewValue() );
			comboboxModel.removeElementAt( index );
			comboboxModel.insertElementAt( evt.getNewValue(), index );
			if ( Application.markerManager.getSelectedMarker() == evt
					.getNewValue() ) {
				showMarkerInfo( (Marker) evt.getNewValue() );
			}
		}

	}

	/**
	 * Listener for Selecting a Marker
	 */
	private class MarkerSelectedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			listMarkers.setSelectedValue( evt.getNewValue(), true );
			showMarkerInfo( (Marker) evt.getNewValue() );
		}
	}
}
