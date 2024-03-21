/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - Occam Labs UG (haftungsbeschränkt) -

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

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.feature.persistence.shape;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.shape.ShapeFeatureStoreProvider.Mapping;
import org.deegree.feature.persistence.shape.jaxb.ShapeFeatureStoreConfig;
import org.deegree.feature.persistence.shape.jaxb.ShapeFeatureStoreConfig.Mapping.GeometryProperty;
import org.deegree.feature.persistence.shape.jaxb.ShapeFeatureStoreConfig.Mapping.SimpleProperty;
import org.deegree.workspace.ResourceBuilder;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO add class documentation here
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */
public class ShapeFeatureStoreBuilder implements ResourceBuilder<FeatureStore> {

	private static final Logger LOG = LoggerFactory.getLogger(ShapeFeatureStoreBuilder.class);

	private ShapeFeatureStoreConfig config;

	private ResourceLocation<FeatureStore> location;

	private ResourceMetadata<FeatureStore> metadata;

	public ShapeFeatureStoreBuilder(ShapeFeatureStoreConfig config, ResourceLocation<FeatureStore> location,
			ResourceMetadata<FeatureStore> metadata) {
		this.config = config;
		this.location = location;
		this.metadata = metadata;
	}

	@Override
	public FeatureStore build() {
		String srs = config.getStorageCRS();
		ICRS crs = null;
		if (srs != null) {
			// rb: if it is null, the shape feature store will try to read
			// the prj files.
			// srs = "EPSG:4326";
			// } else {
			srs = srs.trim();
			crs = CRSManager.getCRSRef(srs);
		}

		String shapeFileName = location.resolveToFile(config.getFile().trim()).toString();

		Charset cs = null;
		String encoding = config.getEncoding();
		if (encoding != null) {
			try {
				cs = Charset.forName(encoding);
			}
			catch (Exception e) {
				String msg = "Unsupported encoding '" + encoding + "'. Continuing with encoding guessing mode.";
				LOG.error(msg);
			}
		}

		List<Mapping> mappings = null;
		if (config.getMapping() != null) {
			mappings = new ArrayList<Mapping>();
			for (Object o : config.getMapping().getSimplePropertyOrGeometryProperty()) {
				if (o instanceof GeometryProperty) {
					GeometryProperty g = (GeometryProperty) o;
					mappings.add(new Mapping(null, g.getName(), false));
				}
				if (o instanceof SimpleProperty) {
					SimpleProperty f = (SimpleProperty) o;
					String name = f.getName();
					if (name == null) {
						name = f.getMapping();
					}
					mappings.add(new Mapping(f.getMapping(), name, f.isGenerateIndex()));
				}
			}
		}

		Boolean genIdx = config.isGenerateAlphanumericIndexes();
		return new ShapeFeatureStore(shapeFileName, crs, cs, config.getFeatureTypeNamespace(),
				config.getFeatureTypeName(), config.getFeatureTypePrefix(), genIdx == null || genIdx, null, mappings,
				metadata);
	}

}
