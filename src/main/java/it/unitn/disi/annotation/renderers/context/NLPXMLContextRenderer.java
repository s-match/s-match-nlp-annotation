package it.unitn.disi.annotation.renderers.context;

import it.unitn.disi.annotation.data.INLPContext;
import it.unitn.disi.annotation.data.INLPNode;
import it.unitn.disi.annotation.data.INLPNodeData;
import it.unitn.disi.nlptools.data.ILabel;
import it.unitn.disi.nlptools.data.IToken;
import it.unitn.disi.smatch.async.AsyncTask;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.data.trees.IBaseNode;
import it.unitn.disi.smatch.renderers.context.BaseXMLContextRenderer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * Renders context with labels.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class NLPXMLContextRenderer extends BaseXMLContextRenderer<INLPContext, INLPNode> implements INLPContextRenderer, IAsyncNLPContextRenderer {

    public NLPXMLContextRenderer() {
        super();
    }

    public NLPXMLContextRenderer(boolean sort) {
        super(sort);
    }

    public NLPXMLContextRenderer(String location, INLPContext context) {
        super(location, context);
    }

    public NLPXMLContextRenderer(String location, INLPContext context, boolean sort) {
        super(location, context, sort);
    }

    protected void renderNodeContents(IBaseNode curNode, TransformerHandler hd) throws SAXException {
        INLPNodeData curNodeData = ((INLPNode) curNode).nodeData();

        if (null != curNodeData.getLabel()) {
            ILabel label = curNodeData.getLabel();
            AttributesImpl atts = new AttributesImpl();
            if (null != label.getText() && !label.getText().equals(curNodeData.getName())) {
                renderAttribute(atts, "text", label.getText());
            }
            hd.startElement("", "", "label", atts);
            renderString(hd, new AttributesImpl(), "formula", label.getFormula());

            if (0 < label.getTokens().size()) {
                atts = new AttributesImpl();
                hd.startElement("", "", "tokens", atts);
                for (IToken token : label.getTokens()) {
                    atts = new AttributesImpl();
                    renderAttribute(atts, "text", token.getText());
                    renderAttribute(atts, "pos", token.getPOSTag());
                    hd.startElement("", "", "token", atts);

                    if (0 < token.getSenses().size()) {
                        hd.startElement("", "", "senses", new AttributesImpl());
                        for (ISense sense : token.getSenses()) {
                            atts = new AttributesImpl();
                            atts.addAttribute("", "", "id", "CDATA", sense.getId());
                            hd.startElement("", "", "sense", atts);
                            hd.endElement("", "", "sense");
                        }
                        hd.endElement("", "", "senses");
                    }
                    hd.endElement("", "", "token");
                }
                hd.endElement("", "", "tokens");
            }
            hd.endElement("", "", "label");
        }
    }

    @Override
    public AsyncTask<Void, INLPNode> asyncRender(INLPContext context, String location) {
        return new NLPXMLContextRenderer(location, context, sort);
    }
}