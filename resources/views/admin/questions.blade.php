@extends('admin.layout.layout')
@section('content')
<div class="container-fluid">
    @if (session('success'))
    <div class="alert alert-success text-center alert-dismissible fade show" role="alert">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    @endif
    <!--  Row 1 -->
    <div class="card w-100">
        <div class="card-body p-4">
            <h5 class="card-title fw-semibold mb-4">Questions</h5>
            {{-- view model --}}
            <div class="table-responsive">
                <table class="table text-nowrap mb-0 align-middle">
                    <thead class="text-dark fs-4">
                        <tr>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">ID</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Question</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Marks</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Challenge</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0 text-center">Action</h6>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        @if (count($questions) === 0)
                        <tr class="text-center">
                            <td class="border-bottom-0" colspan="7">
                                <h6 class="fw-normal mb-0">No questions found</h6>
                            </td>
                        </tr>
                        @endif
                        @foreach ($questions as $question)
                        <tr>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $question->question_id }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $question->text }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $question->marks }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $question->challenge->title }}</p>
                            </td>

                            <td class="border-bottom-0">
                                <p class="fw-bold mb-0 text-center">
                                    <span class="text-success" data-bs-toggle="modal" data-bs-target="#exampleModalview{{ $question->question_id }}">
                                        <b><i class="ti ti-eye"> view</i></b>
                                    </span>
                                </p>
                            </td>

                            <!-- view Modal -->
                            <div class="modal fade" id="exampleModalview{{ $question->question_id }}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel{{ $question->question_id }}">Question Details
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p><span class="text-primary">ID: </span>{{ $question->question_id }}</p>
                                            <p><span class="text-primary">Question: </span>{{ $question->text}}
                                            </p>
                                            <p><span class="text-primary">Marks:
                                                </span>{{ $question->marks }}</p>
                                            <p><span class="text-primary">Challenge:
                                                </span>{{ $question->challenge->title }}</p>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {{-- end view model --}}
                        </tr>
                        @endforeach

                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>
@endsection


