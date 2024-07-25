<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::create('attempts', function (Blueprint $table) {
            $table->id('attempt_id');
            $table->unsignedBigInteger('participant_id');
            $table->unsignedBigInteger('challenge_id');
            $table->timestamp('start_time')->nullable();
            $table->timestamp('end_time')->nullable();
            $table->unsignedInteger('total_time_taken');
            $table->integer('total_score');
            $table->timestamps();

            $table->foreign('participant_id')->references('participant_id')->on('participants')->onDelete('cascade');
            $table->foreign('challenge_id')->references('challenge_id')->on('challenges')->onDelete('cascade');
        });

        Schema::enableForeignKeyConstraints();
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::disableForeignKeyConstraints();

        Schema::dropIfExists('attempts');

        Schema::enableForeignKeyConstraints();
    }
};
