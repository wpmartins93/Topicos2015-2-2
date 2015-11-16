package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Wellington on 28/09/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;
    private World mundo;
    private Body chao; // corpo chão
    private Passaro passaro;
    private Box2DDebugRenderer debug;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrthographicCamera cameraInfo;

    private Texture[] texturasPassaro;
    private Texture texturaObstaculoCima;
    private Texture texturaObstaculoBaixo;
    private Texture texturaChao;
    private Texture texturaFundo;
    private Texture texturaPlay;
    private Texture texturaGameOver;

    private boolean jogoIniciado = false;

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA );
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f), false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        initTexturas();
        initChao();
        initPassaro();
        initFontes();
        initInformacoes();

    }

    private void initTexturas() {
        texturasPassaro = new Texture[3];
        texturasPassaro[0] = new Texture("sprites/bird-1.png");
        texturasPassaro[1] = new Texture("sprites/bird-2.png");
        texturasPassaro[2] = new Texture("sprites/bird-3.png");

        texturaObstaculoCima = new Texture("sprites/toptube.png");
        texturaObstaculoBaixo = new Texture("sprites/bottomtube.png");

        texturaFundo = new Texture("sprites/bg.png");
        texturaChao = new Texture("sprites/ground.png");

        texturaPlay = new Texture("sprites/playbtn.png");
        texturaGameOver = new Texture("sprites/gameover.png");

    }

    private boolean gameOver = false;
    /**
     * Verifica se o Pássaro está envolvido na colisão
     * @param fixtureA
     * @param fixtureB
     */
    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if ("PASSARO".equals(fixtureA.getUserData()) || ("PASSARO".equals(fixtureB.getUserData()))){
            // game over
            gameOver = true;
        }
    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fontePontuacao = gerador.generateFont(fonteParam);

        gerador.dispose();
    }

    private void initInformacoes() {
        palcoInformacoes = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));

        Gdx.input.setInputProcessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

        ImageButton.ImageButtonStyle estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturaPlay));

        btnPlay = new ImageButton(estiloBotao);
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                jogoIniciado = true;
            }
        });
        palcoInformacoes.addActor(btnPlay);


        estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturaGameOver));

        btnGameOver = new ImageButton(estiloBotao);
        btnGameOver.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reiniciarJogo();
            }
        });

        palcoInformacoes.addActor(btnGameOver);
    }

    private void reiniciarJogo(){
        game.setScreen(new TelaJogo(game));
    }
    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);

    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturaTeclas();
        atualizar(delta);
        renderizar(delta);

        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }

    private  boolean pulando = false;
    private void capturaTeclas() {
        pulando = false;
        if (Gdx.input.justTouched()){
            pulando = true;
        }
    }

    /**
     * Renderizar
     * @param delta
     */
    private void renderizar(float delta) {
        palcoInformacoes.draw();
    }

    private void atualizar(float delta) {
        palcoInformacoes.act(delta);
        passaro.getCorpo().setFixedRotation(!gameOver);
        passaro.atualizar(delta, !gameOver);
        if (jogoIniciado){
            mundo.step(1f / 60f, 6, 2);
            atualizarObstaculos();
        }

        atualizarInformacoes();


        if (!gameOver){
            atualizarCamera();
            atualizarChao();
        }


        if (pulando && !gameOver && jogoIniciado){
            passaro.pular();
        }
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText(pontuacao + "");
        lbPontuacao.setPosition(
                cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2,
                cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());

        btnPlay.setPosition(
                cameraInfo.viewportWidth / 2 - btnPlay.getPrefWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnPlay.getPrefHeight() * 2
        );
        btnPlay.setVisible(!jogoIniciado);

        btnGameOver.setPosition(
                cameraInfo.viewportWidth / 2 - btnGameOver.getPrefWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnGameOver.getPrefHeight() / 2
        );
        btnGameOver.setVisible(gameOver);

    }

    private void atualizarObstaculos() {
        // enquanto a lista conter menos do que 4 obstaculos, crie obstáculos
        while (obstaculos.size < 4){
            Obstaculo ultimoObs = null;
            if (obstaculos.size > 0) {
                ultimoObs = obstaculos.peek(); // recupera o ultimo item da lista
            }
            Obstaculo obs = new Obstaculo(mundo, camera, ultimoObs);
            obstaculos.add(obs);
        }

        // verifica se os obstáculos sairam da tela para removê-los
        for (Obstaculo obs : obstaculos){
            // calcula a lateral inicial da câmera
            float inicioCamera = passaro.getCorpo().getPosition().x - (camera.viewportWidth / 2 / Util.PIXEL_METRO) - obs.getLargura();
            // verifica se o obstáculo saiu da tela
            if (inicioCamera > obs.getPosX()){
                obs.removerObstaculos();
                obstaculos.removeValue(obs, true);
            } else if (!obs.isPassou() && obs.getPosX() < passaro.getCorpo().getPosition().x){
                obs.setPassou(true);
                // calcular pontuação
                pontuacao++;
                // reproduzir o som
            }
        }
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + 34 / Util.PIXEL_METRO)* Util.PIXEL_METRO;
        camera.update();
    }

    /**
     * Atualiza a posição do chão para acompanhar o passaro
     */
    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();

        chao.setTransform(posicao.x, 0, 0);

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
        redimendionaChao();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
    }

    /**
     * Configura o tamanho do chão de acordo com o tamanho da tela
     */
    private void redimendionaChao() {
        chao.getFixtureList().clear();
        float largura = camera.viewportHeight / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao, shape, "CHAO");
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        debug.dispose();
        mundo.dispose();
        palcoInformacoes.dispose();
        fontePontuacao.dispose();

        texturasPassaro[0].dispose();
        texturasPassaro[1].dispose();
        texturasPassaro[2].dispose();

        texturaObstaculoCima.dispose();
        texturaObstaculoBaixo.dispose();

        texturaFundo.dispose();
        texturaChao.dispose();

        texturaPlay.dispose();
        texturaGameOver.dispose();
    }
}
