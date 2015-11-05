package com.spartansoftwareinc.ws.okapi.filters;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idiominc.wssdk.WSRuntimeException;
import com.idiominc.wssdk.component.filter.WSFilterConfigurationData;

import net.sf.okapi.common.IParameters;

/**
 * Base class for a WSFilterConfigurationData instance that stores an Okapi
 * IParameters instance using its internal String representation.
 */
public abstract class WSOkapiFilterConfigurationData<T extends IParameters> extends WSFilterConfigurationData {
    private static final long serialVersionUID = 1L;

    private String serializedParams;

    protected abstract T getDefaultParameters();

    public WSOkapiFilterConfigurationData() {
        setParameters(getDefaultParameters());
    }

    public WSOkapiFilterConfigurationData(T params) {
        setParameters(params);
    }

    public void setParameters(T params) {
        this.serializedParams = params.toString();
    }

    public T getParameters() {
        T params = getDefaultParameters();
        if (serializedParams != null) {
            params.fromString(serializedParams);
        }
        return params;
    }

    @Override
    public Node toXML() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().newDocument();
            Node top = doc.createElement("params");
            Node okapiParams = top.appendChild(doc.createElement("okapi"));
            // Guard against config corruption, which has happened in dev but
            // is probably impossible in the real world
            if (serializedParams == null) {
                serializedParams = getDefaultParameters().toString();
            }
            okapiParams.appendChild(doc.createCDATASection(serializedParams));
            saveAdditionalConfiguration(doc, top);
            return top;
        }
        catch (Exception e) {
            throw new WSRuntimeException(e);
        }
    }

    @Override
    public void fromXML(Node xmlData) {
        NodeList nl = xmlData.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeName().equals("okapi")) {
                this.serializedParams = n.getNodeValue();
            }
            else {
                loadAdditionalConfiguration(n);
            }
        }
    }

    protected void saveAdditionalConfiguration(Document doc, Node parent) {
    }

    protected void loadAdditionalConfiguration(Node configNode) {
    }
}
