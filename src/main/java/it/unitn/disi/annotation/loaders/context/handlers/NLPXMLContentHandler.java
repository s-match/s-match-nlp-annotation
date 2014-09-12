package it.unitn.disi.annotation.loaders.context.handlers;

import it.unitn.disi.annotation.data.INLPContext;
import it.unitn.disi.annotation.data.INLPNode;
import it.unitn.disi.annotation.data.NLPContext;
import it.unitn.disi.nlptools.data.ILabel;
import it.unitn.disi.nlptools.data.IToken;
import it.unitn.disi.nlptools.data.Label;
import it.unitn.disi.nlptools.data.Token;
import it.unitn.disi.smatch.data.ling.ISense;
import it.unitn.disi.smatch.loaders.context.handlers.BaseXMLContentHandler;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;
import it.unitn.disi.smatch.oracles.LinguisticOracleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class NLPXMLContentHandler extends BaseXMLContentHandler<INLPContext> {

    private static final Logger log = LoggerFactory.getLogger(NLPXMLContentHandler.class);

    private final ILinguisticOracle oracle;

    // path to the root node
    protected final Deque<INLPNode> pathToRoot = new ArrayDeque<>();

    // label being read
    private ILabel label;
    // token being read
    private IToken token;

    public NLPXMLContentHandler() {
        super();
        this.oracle = null;
    }

    public NLPXMLContentHandler(boolean uniqueStrings) {
        super(uniqueStrings);
        this.oracle = null;
    }

    public NLPXMLContentHandler(ILinguisticOracle oracle) {
        super();
        this.oracle = oracle;
    }

    public NLPXMLContentHandler(ILinguisticOracle oracle, boolean uniqueStrings) {
        super(uniqueStrings);
        this.oracle = oracle;
    }

    @Override
    public void startDocument() {
        super.startDocument();
        ctx = new NLPContext();
    }

    @Override
    public void startElement(String namespace, String localName, String qName, Attributes atts) {
        switch (localName) {
            case "node":
                INLPNode node;
                if (null == ctx.getRoot()) {
                    node = ctx.createRoot();
                } else {
                    if (0 < pathToRoot.size()) {
                        node = pathToRoot.getLast().createChild();
                    } else {
                        // looks like there are multiple roots
                        INLPNode oldRoot = ctx.getRoot();
                        INLPNode newRoot = ctx.createRoot("Top");
                        newRoot.addChild(oldRoot);
                        node = newRoot.createChild();
                    }
                }
                node.getNodeData().setId(atts.getValue("id"));
                pathToRoot.addLast(node);
                break;
            case "label":
                label = new Label();
                INLPNode n = pathToRoot.getLast();
                n.getNodeData().setLabel(label);
                String text = atts.getValue("text");
                if (null == text) {
                    text = makeUnique(n.getNodeData().getName());
                } else {
                    text = makeUnique(text);
                }
                label.setText(text);
                break;
            case "token":
                if (null != label) {
                    token = new Token();
                    if (0 == label.getTokens().size()) {
                        label.setTokens(new ArrayList<IToken>());
                    }
                    token.setText(atts.getValue("text"));
                    token.setPOSTag(atts.getValue("pos").intern());
                    label.getTokens().add(token);
                }
                break;
            case "sense":
                if (null != oracle) {
                    if (0 == token.getSenses().size()) {
                        token.setSenses(new ArrayList<ISense>());
                    }
                    try {
                        token.getSenses().add(oracle.createSense(atts.getValue("id")));
                    } catch (LinguisticOracleException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
                break;
            default:
                content = new StringBuilder();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (localName) {
            case "name":
                pathToRoot.getLast().getNodeData().setName(makeUnique(content.toString()));
                break;
            case "formula":
                if (null != label) {
                    label.setFormula(content.toString());
                }
                break;
            case "label":
                label = null;
                break;
            case "token":
                token = null;
                break;
            case "node":
                pathToRoot.removeLast();

                nodesParsed++;
                if (0 == (nodesParsed % 1000)) {
                    log.info("nodes parsed: " + nodesParsed);
                }
                break;
        }
    }

    @Override
    public void endDocument() {
        super.endDocument();
        pathToRoot.clear();
    }
}