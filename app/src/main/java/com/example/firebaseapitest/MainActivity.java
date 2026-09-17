package com.example.firebaseapitest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etIdCurso, etNombreCurso, etGrado;
    private TextView tvResultado, tvLimpiar;
    private Button btnGuardar, btnActualizar, btnListar, btnEliminar, btnBuscarId;
    private final String BASE_URL = "https://apptestapi-3d114-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIdCurso = findViewById(R.id.etIdCurso);
        etNombreCurso = findViewById(R.id.etNombreCurso);
        etGrado = findViewById(R.id.etGrado);
        tvResultado = findViewById(R.id.tvResultado);
        tvLimpiar = findViewById(R.id.tvLimpiar);

        btnGuardar = findViewById(R.id.btnRegistrar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnListar = findViewById(R.id.btnListar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscarId = findViewById(R.id.btnBuscarId);

        setBotonesEdicionActivos(false);

        btnGuardar.setOnClickListener(v -> guardarCursoIncremental());
        btnBuscarId.setOnClickListener(v -> buscarCursoPorId());
        btnActualizar.setOnClickListener(v -> ejecutarAccionRed("PUT", etIdCurso.getText().toString().trim()));
        btnListar.setOnClickListener(v -> ejecutarAccionRed("GET_ALL", ""));
        btnEliminar.setOnClickListener(v -> ejecutarAccionRed("DELETE", etIdCurso.getText().toString().trim()));
        tvLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    private void setBotonesEdicionActivos(boolean activo) {
        btnActualizar.setEnabled(activo);
        btnEliminar.setEnabled(activo);
        btnActualizar.setAlpha(activo ? 1.0f : 0.5f);
        btnEliminar.setAlpha(activo ? 1.0f : 0.5f);
    }

    private void limpiarCampos() {
        etIdCurso.setText("");
        etNombreCurso.setText("");
        etGrado.setText("");
        setBotonesEdicionActivos(false);
        tvResultado.setText("Campos limpios.");
    }

    private void guardarCursoIncremental() {
        String nombre = etNombreCurso.getText().toString().trim();
        String grado = etGrado.getText().toString().trim();

        if (nombre.isEmpty() || grado.isEmpty()) {
            tvResultado.setText("Completa el nombre y el grado.");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        tvResultado.setText("Calculando ID y guardando...");

        executor.execute(() -> {
            try {
                URL urlGet = new URL(BASE_URL + "cursos.json");
                HttpURLConnection connGet = (HttpURLConnection) urlGet.openConnection();
                connGet.setRequestMethod("GET");

                int nuevoId = 1;
                if (connGet.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connGet.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    String json = sb.toString();
                    if (!json.equals("null") && !json.isEmpty()) {
                        int maxId = 0;
                        if (json.startsWith("{")) {
                            JSONObject obj = new JSONObject(json);
                            Iterator<String> keys = obj.keys();
                            while (keys.hasNext()) {
                                try {
                                    int keyInt = Integer.parseInt(keys.next());
                                    if (keyInt > maxId) maxId = keyInt;
                                } catch (NumberFormatException ignored) {}
                            }
                        } else if (json.startsWith("[")) {
                            JSONArray arr = new JSONArray(json);
                            for (int i = 0; i < arr.length(); i++) {
                                if (!arr.isNull(i) && i > maxId) maxId = i;
                            }
                        }
                        nuevoId = maxId + 1;
                    }
                }

                URL urlPut = new URL(BASE_URL + "cursos/" + nuevoId + ".json");
                HttpURLConnection connPut = (HttpURLConnection) urlPut.openConnection();
                connPut.setRequestMethod("PUT");
                connPut.setRequestProperty("Content-Type", "application/json; utf-8");
                connPut.setDoOutput(true);

                String jsonBody = "{\"nombreCurso\":\"" + nombre + "\", \"grado\":\"" + grado + "\"}";
                try (OutputStream os = connPut.getOutputStream()) {
                    os.write(jsonBody.getBytes("utf-8"));
                }

                if (connPut.getResponseCode() == HttpURLConnection.HTTP_OK || connPut.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    final int finalNuevoId = nuevoId;
                    runOnUiThread(() -> {
                        etIdCurso.setText(String.valueOf(finalNuevoId));
                        setBotonesEdicionActivos(true);
                        tvResultado.setText("¡Guardado exitoso con ID: " + finalNuevoId + "!");
                    });
                } else {
                    runOnUiThread(() -> tvResultado.setText("Error al guardar."));
                }

            } catch (Exception e) {
                runOnUiThread(() -> tvResultado.setText("Excepción: " + e.getMessage()));
            }
        });
    }

    private void buscarCursoPorId() {
        String id = etIdCurso.getText().toString().trim();
        if (id.isEmpty()) {
            tvResultado.setText("Ingresa un ID para buscar.");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        tvResultado.setText("Buscando...");

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "cursos/" + id + ".json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    String json = sb.toString();
                    if (json.equals("null") || json.isEmpty()) {
                        runOnUiThread(() -> {
                            tvResultado.setText("No se encontró el curso con ID " + id);
                            setBotonesEdicionActivos(false);
                        });
                    } else {
                        JSONObject obj = new JSONObject(json);
                        String nombre = obj.optString("nombreCurso");
                        String grado = obj.optString("grado");

                        runOnUiThread(() -> {
                            etNombreCurso.setText(nombre);
                            etGrado.setText(grado);
                            setBotonesEdicionActivos(true);
                            tvResultado.setText("¡Curso encontrado!");
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> tvResultado.setText("Excepción: " + e.getMessage()));
            }
        });
    }

    private void ejecutarAccionRed(String accion, String id) {
        if ((accion.equals("PUT") || accion.equals("DELETE")) && id.isEmpty()) {
            tvResultado.setText("Falta el ID.");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        tvResultado.setText("Procesando...");

        executor.execute(() -> {
            try {
                String urlString = accion.equals("GET_ALL") ? BASE_URL + "cursos.json" : BASE_URL + "cursos/" + id + ".json";
                String metodo = accion.equals("GET_ALL") ? "GET" : accion;

                HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setRequestMethod(metodo);

                if (metodo.equals("PUT")) {
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);
                    String nombre = etNombreCurso.getText().toString().trim();
                    String grado = etGrado.getText().toString().trim();
                    String jsonBody = "{\"nombreCurso\":\"" + nombre + "\", \"grado\":\"" + grado + "\"}";
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonBody.getBytes("utf-8"));
                    }
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    if (metodo.equals("GET")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) sb.append(line);
                        reader.close();

                        String json = sb.toString();
                        String resultadoFormateado = "Sin registros.";

                        if (!json.equals("null") && !json.isEmpty()) {
                            if (json.startsWith("[")) {
                                JSONArray arr = new JSONArray(json);
                                StringBuilder sbCustom = new StringBuilder("{\n");
                                for (int i = 0; i < arr.length(); i++) {
                                    if (!arr.isNull(i)) {
                                        sbCustom.append("  \"ID: ").append(i).append("\": ").append(arr.getJSONObject(i).toString(4)).append(",\n");
                                    }
                                }
                                sbCustom.append("}");
                                resultadoFormateado = sbCustom.toString();
                            } else if (json.startsWith("{")) {
                                JSONObject obj = new JSONObject(json);
                                Iterator<String> keys = obj.keys();
                                StringBuilder sbCustom = new StringBuilder("{\n");
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    sbCustom.append("  \"ID: ").append(key).append("\": ").append(obj.getJSONObject(key).toString(4)).append(",\n");
                                }
                                sbCustom.append("}");
                                resultadoFormateado = sbCustom.toString();
                            } else {
                                resultadoFormateado = json;
                            }
                        }

                        final String finalResultado = resultadoFormateado;
                        runOnUiThread(() -> tvResultado.setText(finalResultado));
                    } else if (metodo.equals("DELETE")) {
                        runOnUiThread(() -> {
                            tvResultado.setText("¡Eliminado con éxito!");
                            limpiarCampos();
                        });
                    } else {
                        runOnUiThread(() -> tvResultado.setText("¡Actualización exitosa!"));
                    }
                } else {
                    runOnUiThread(() -> tvResultado.setText("Error en la operación."));
                }
            } catch (Exception e) {
                runOnUiThread(() -> tvResultado.setText("Excepción: " + e.getMessage()));
            }
        });
    }
}